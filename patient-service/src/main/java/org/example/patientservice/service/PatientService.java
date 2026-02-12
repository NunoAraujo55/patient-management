package org.example.patientservice.service;

import org.example.patientservice.dto.PatientRequestDTO;
import org.example.patientservice.dto.PatientResponseDTO;
import org.example.patientservice.execption.EmailAlreadyExistsException;
import org.example.patientservice.execption.PatientNotFoundException;
import org.example.patientservice.grpc.BillingServiceGrpcClient;
import org.example.patientservice.kafka.KafkaProducer;
import org.example.patientservice.mapper.PatientMapper;
import org.example.patientservice.model.Patient;
import org.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients
                .stream()
                .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with the email " + patientRequestDTO.getEmail() + " already exists");
        }
        Patient newpatient = patientRepository
                .save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(
                newpatient.getId().toString(),
                newpatient.getName(),
                newpatient.getEmail()
        );

        kafkaProducer.sendEvent(newpatient);

        return PatientMapper.toDTO(newpatient);
    }

    public PatientResponseDTO updatePatient(
            UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " +  id)
        );

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with the email " + patientRequestDTO.getEmail() + " already exists");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDataOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void delete(UUID id){
        patientRepository.deleteById(id);
    }

}
