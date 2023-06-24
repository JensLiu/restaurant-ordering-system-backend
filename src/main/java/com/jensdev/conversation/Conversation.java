package com.jensdev.conversation;

import com.jensdev.user.modal.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation")
public class Conversation {
    /**
     * We only support PMs between two users
     */
    @Id
    @GeneratedValue
    long id;
    @OneToMany
    List<User> participants;
    Date createdAt;
    @OneToMany
    List<Message> messages;
}
