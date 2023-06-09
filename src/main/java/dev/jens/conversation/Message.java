package dev.jens.conversation;

import dev.jens.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue
    long id;
    String body;
    String imageSrc;
    @OneToOne
    User sender;
    Date sentAt;
    Date seenAt;
}
