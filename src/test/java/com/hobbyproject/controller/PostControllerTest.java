package com.hobbyproject.controller;

import com.hobbyproject.entity.Member;
import com.hobbyproject.entity.Post;
import com.hobbyproject.repository.MemberRepository;
import com.hobbyproject.repository.PostRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("post화면에서 list의 갯수가 제대로 나오는지 확인하는 테스트")
    void postListCheck() throws Exception {
        //given
        Member member = Member.builder()
                .loginId("1")
                .password("1")
                .name("이름")
                .birthday(LocalDate.parse("2000-11-11"))
                .build();

        Post post1 = Post.builder()
                .title("제목1")
                .content("내용1")
                .member(member)
                .build();
        Post post2 = Post.builder()
                .title("제목2")
                .content("내용2")
                .member(member)
                .build();
        Post post3 = Post.builder()
                .title("제목3")
                .content("내용3")
                .member(member)
                .build();

        memberRepository.save(member);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/post"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("posts", Matchers.hasSize(3)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("postview화면에서 클릭한 정보가 제대로 들어왔는가?")
    void postCheck() throws Exception {
        //given
        Member member = Member.builder()
                .loginId("1")
                .password("1")
                .name("이름")
                .birthday(LocalDate.parse("2000-11-11"))
                .build();

        Post post1 = Post.builder()
                .title("제목1")
                .content("내용1")
                .member(member)
                .build();
        memberRepository.save(member);
        Post post = postRepository.save(post1);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("memberId",true);

        mockMvc.perform(MockMvcRequestBuilders.get("/post/{postId}",post.getPostId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("post", Matchers.hasProperty("title", Matchers.is("제목1"))))
                .andExpect(MockMvcResultMatchers.model().attribute("post", Matchers.hasProperty("content", Matchers.is("내용1"))))
                .andDo(MockMvcResultHandlers.print());
    }
}