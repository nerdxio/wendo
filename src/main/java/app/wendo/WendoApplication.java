package app.wendo;

import app.wendo.users.models.*;
import app.wendo.users.repositories.DriverRepository;
import app.wendo.users.repositories.PassengerRepository;
import app.wendo.users.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@SpringBootApplication
public class WendoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WendoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(
            UserRepository userRepository,
            DriverRepository driverRepository,
            PassengerRepository passengerRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Create Driver User
            var driverUser = User.builder()
                    .fullName("Hassan Driver")
                    .phoneNumber("01221736193")
                    .password(passwordEncoder.encode("pass"))
                    .role(Role.DRIVER)
                    .email("driver@example.com")
                    .isOnline(true)
                    .registrationStatus(RegistrationStatus.REGISTRATION_COMPLETE)
                    .build();

            var savedDriverUser = userRepository.save(driverUser);

            var driver = Driver.builder()
                    .user(savedDriverUser)
                    .isAvailable(true)
                    .rating(4.5)
                    .totalTrips(0)
                    .nationalIdFront("id_front_url")
                    .nationalIdBack("id_back_url")
                    .driverLicenseFrontPicture("license_front_url")
                    .driverLicenseBackPicture("license_back_url")
                    .build();

            driverRepository.save(driver);

            // Create Passenger User
            var passengerUser = User.builder()
                    .fullName("Jane Passenger")
                    .phoneNumber("01221736194")
                    .password(passwordEncoder.encode("pass"))
                    .role(Role.PASSENGER)
                    .email("passenger@example.com")
                    .isOnline(true)
                    .registrationStatus(RegistrationStatus.REGISTRATION_COMPLETE)
                    .build();

            var savedPassengerUser = userRepository.save(passengerUser);

            var passenger = Passenger.builder()
                    .user(savedPassengerUser)
                    .totalTrips(0)
                    .nationalIdFront("id_front_url")
                    .nationalIdBack("id_back_url")
                    .build();

            passengerRepository.save(passenger);
        };
    }

}
