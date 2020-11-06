package com.sandeep.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.FetchType.LAZY;

/** @Data Responsible to generate getters and setters
 * @AllArgsConstructor and @NoArgsConstructor are responsible to generate constructors
 * @Builder it will generate builder methods for our class.
 */

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long postId;
        @NotBlank(message = "Post Name cannot be empty or Null")
        private String postName;
        @Nullable
        private String url;
        @Nullable
        @Lob
        private String description;
        private Integer voteCount = 0;
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "userId", referencedColumnName = "userId")
        private User user;
        private Instant createdDate;
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "id", referencedColumnName = "id")
        private Subreddit subreddit;

}
