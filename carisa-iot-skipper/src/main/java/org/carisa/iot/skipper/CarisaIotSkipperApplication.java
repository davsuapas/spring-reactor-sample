package org.carisa.iot.skipper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;
import org.springframework.cloud.skipper.server.EnableSkipperServer;

/**
 * Runs the Skipper Server Application.
 *
 * @author David Suárez
 *
 */
@SpringBootApplication(exclude = {
		CloudFoundryDeployerAutoConfiguration.class,
		KubernetesAutoConfiguration.class,
		LocalDeployerAutoConfiguration.class,
		SecurityAutoConfiguration.class,
		SessionAutoConfiguration.class
})
@EnableSkipperServer
public class CarisaIotSkipperApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarisaIotSkipperApplication.class, args);
	}
}

