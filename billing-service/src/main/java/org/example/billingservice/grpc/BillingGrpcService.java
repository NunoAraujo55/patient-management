package org.example.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(
            billing.BillingRequest billingRequest,
            StreamObserver<billing.BillingResponse> responseObserver //once the client opens a connection it allows ti keep sending and receiving to the client
    ){
        log.info("createBillingAccount request received {}", billingRequest.toString());

        //Business logic - e.g save to db, perform ...

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("1234")
                .setStatus("Active")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
