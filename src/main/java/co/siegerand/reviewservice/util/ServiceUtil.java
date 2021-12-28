package co.siegerand.reviewservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServiceUtil {

    private final String serviceAddress;

    @Autowired
    public ServiceUtil(@Value("${server.port}") String port) {
        serviceAddress = getHostNameAndIpAddress().concat(":").concat(port);
    }

    public String getServiceAddress() {
        return serviceAddress;
    }


    private String getHostNameAndIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostName()
                    .concat("/")
                    .concat(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            return "Unknown Host";
        }
    }
}
