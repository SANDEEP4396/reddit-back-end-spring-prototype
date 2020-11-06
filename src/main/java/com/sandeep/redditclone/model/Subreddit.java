package com.sandeep.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;


/** @Data Responsible to generate getters and setters
 * @AllArgsConstructor and @NoArgsConstructor are responsible to generate constructors
 * @Builder it will generate builder methods for our class.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Subreddit {
    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Long id;
    @NotBlank(message = "Community name is required")
    private String name;
   @NotBlank(message = "Description is required")
    private String description;
    @OneToMany(fetch = LAZY)
    private List<Post> posts;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    private User user;
}