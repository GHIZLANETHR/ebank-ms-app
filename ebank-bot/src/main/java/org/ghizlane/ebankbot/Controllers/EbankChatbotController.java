package org.ghizlane.ebankbot.Controllers;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.security.PublicKey;

@RestController
public class EbankChatbotController {
    private ChatClient chatClient;

    public EbankChatbotController(ChatClient.builder chatClient) {
        this.chatClient=chatClient.build();
    }
    @GetMapping("/chat")
    public String chat(  @RequestParam(name = "query", defaultValue = "Bonjour")
                             String query){
        return chatClient.prompt(query).call().content();
    }
    @GetMapping(value = "/chatStream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chatStream(
            @RequestParam(name = "query", defaultValue = "Bonjour") String query){
        return ebankAIAgent.chatStream(new Prompt(query));
    }

}
