package com.sandeep.redditclone.repository;

import com.sandeep.redditclone.model.Post;
import com.sandeep.redditclone.model.User;
import com.sandeep.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}