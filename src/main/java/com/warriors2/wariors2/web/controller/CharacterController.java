package com.warriors2.wariors2.web.controller;

import com.warriors2.wariors2.characterForm.CharacterForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.warriors2.wariors2.model.Character;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CharacterController {

    @Value("${welcome.message}")
    private String message;

    @Value("${error.message}")
    private String errorMessage;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

    @RequestMapping(value = {"/characterList"}, method = RequestMethod.GET)
    public String characterList(Model model) {

        List<Character> characters = new RestTemplate().getForObject("http://localhost:8080/characters", List.class);

        model.addAttribute("characters", characters);

        return "characterList";
    }

    @RequestMapping(value = {"/addCharacter"}, method = RequestMethod.GET)
    public String showAddCharacterPage(Model model) {

        CharacterForm characterForm = new CharacterForm();
        model.addAttribute("characterForm", characterForm);

        return "addCharacter";
    }

    @RequestMapping(value = { "/addCharacter" }, method = RequestMethod.POST)
    public String saveCharacter(Model model,
                             @ModelAttribute("characterForm") CharacterForm characterForm) {

        List<Character> characters = new RestTemplate().getForObject("http://localhost:8080/characters", List.class);

        int id = characters.size();
        String name = characterForm.getName();
        String job = characterForm.getJob();

        if (name != null && name.length() > 0
                && job != null && job.length() > 0
        ) {
            Character newCharacter = new Character(id, name, job);
            new RestTemplate().postForObject("http://localhost:8080/character", newCharacter,Character.class);

            return "redirect:/characterList";
        }

        model.addAttribute("errorMessage", errorMessage);
        return "addCharacter";
    }

    @RequestMapping(value = { "/deleteCharacter/{id}" }, method = RequestMethod.GET)
    public String deleteCharacter(@PathVariable int id) {

        new RestTemplate().delete("http://localhost:8080/character/" + id);

        return "redirect:/characterList";
    }

    @RequestMapping(value = { "/editCharacter/{id}" }, method = RequestMethod.GET)
    public String editCharacter(@PathVariable int id, Model model) {

        Character[] characters = new RestTemplate().getForObject("http://localhost:8080/characters", Character[].class);

        for (Character character : characters) {
            if (character.getId() == id ) {
                CharacterForm characterForm = new CharacterForm(character.getId(),character.getName(),character.getJob());
                model.addAttribute("characterForm", characterForm);
                return "editCharacter";
            }
        }
        return "characterList";
    }

    @RequestMapping(value = { "/editCharacter/{id}" }, method = RequestMethod.POST)
    public String editCharacter (@ModelAttribute("characterForm") CharacterForm characterForm, @PathVariable int id){

        Character[] characters = new RestTemplate().getForObject("http://localhost:8080/characters", Character[].class);
        for (Character character : characters) {
            if (character.getId() == id ) {
                character.setName(characterForm.getName());
                character.setJob(characterForm.getJob());
                new RestTemplate().put("http://localhost:8080/character/" + character.getId(), character, Character.class);
                return "redirect:/characterList";
            }
        }
        return "editCharacter";
    }

}
