package com.petbuddyz.petbuddyzapp.Community;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;

@Service
public class CommunityService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommunityRepository communityRepository;

    private static final Logger logger = LogManager.getLogger(CommunityService.class);

    public List<Community> getAllCommunities() {
        logger.info("Getting all communities");
        return communityRepository.findAll();
    }

    public Community createCommunity(Community community) {
        logger.info("Creating community: {}", community);
        return communityRepository.save(community);
    }

    public Community getCommunity(Long id) {
        logger.info("Getting community with id: {}", id);
        return communityRepository.findById(id).orElseThrow(() -> new CommunityNotFoundException("Community not found"));
    }

    public List<Owner> getCommunityMembers(Long id) {
        logger.info("Getting community members for community with id: {}", id);
        Community community = communityRepository.findById(id).orElseThrow(() -> new CommunityNotFoundException("Community not found"));
        return community.getCommunityMembers();
    }

    public List<Post> getCommunityPosts(Long communityId) {
        return postRepository.findByCommunity_CommunityId(communityId);
    }

    public void deleteCommunity(Long communityid) {
        logger.info("Deleting community with id: {}", communityid);
        communityRepository.deleteById(communityid);
    }

}
