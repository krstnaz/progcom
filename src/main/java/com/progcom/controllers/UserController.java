package com.progcom.controllers;

import com.progcom.domain.Role;
import com.progcom.domain.User;
import com.progcom.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
//Мэпинг для всего класса, чтобы во всех запросах не писать путь
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
//Класс для работы из админки
public class UserController
{
    //Подключаем репозиторий пользователей
    @Autowired
    private UserRepo userRepo;

    //Выводим в админке список пользователей и их параметры
    @GetMapping
    public String userList(Model model)
    {
        model.addAttribute("users", userRepo.findAll());

        return "userList";
    }


    //Переходим по id в редактор пользователя, выводим страницу редактирования для конкретного пользователя
    @GetMapping("{user}")
    public String getUserEditForm(@PathVariable User user, Model model)
    {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    //Добавляем в параметры пользователя отредактированные поля
    //поля вытаскиваем из формы
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user)
    {
        user.setUsername(username);
        //Сохраняем набор существующих ролей
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        //очищаем роли пользователя, чтобы не дублировать информацию
        user.getRoles().clear();

        //перебираем формы для нахождения значения роли, если роль установлена, то сохраняем ее в параметры пользователя
        for(String key : form.keySet())
        {
            if(roles.contains(key))
            {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);
        return "redirect:/user";
    }


}
