
package com.petbuddyz.petbuddyzapp.Like;


import java.util.Date;

import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a like on a post or comment.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class Like {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long likeId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;

    
    @Temporal(TemporalType.TIMESTAMP)
    private Date likeCreationDate;

     @PrePersist
    protected void onCreate() {
        Date date = new Date();
        likeCreationDate = date;
    }
}

