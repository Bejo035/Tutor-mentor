package ge.batumi.tutormentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class TutorMentorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TutorMentorApplication.class, args);
	}

}
