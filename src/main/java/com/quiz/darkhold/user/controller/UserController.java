package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.init.FileUploadUtil;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import com.quiz.darkhold.user.service.UserService;
import com.quiz.darkhold.user.service.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class UserController {
    private final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * on to create challenge page.
     *
     * @return create challenge page
     */
    @GetMapping("/userManagement")
    public String manageUsers(final Model model) {
        return listByPage(1, model);
    }

    @RequestMapping("/user/page/{pageNumber}")
    public String listByPage(final @PathVariable(name = "pageNumber") int pageNumber,
                             final Model model) {
        logger.info("ListByPage method of user controller ");
        var page = userService.getAllUsers(pageNumber);
        var startCount = (pageNumber - 1) * UserServiceImpl.USERS_PER_PAGE + 1;
        long endCount = (long) startCount + UserServiceImpl.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listusers", page.getContent());
        return "user/usermanagement";
    }

    @GetMapping("/user/create")
    public String createUser(final Model model) {
        logger.info("Into the createUser method");
        var roles = userService.listRoles();
        var user = new User();
        user.setEnabled(true);
        model.addAttribute("userForm", user);
        model.addAttribute("listRoles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "user/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(final User user, final RedirectAttributes redirectAttributes,
                           @RequestParam("image") final MultipartFile multipartFile) throws IOException {
        logger.info("Into the saveUser method, user is {}", user);
        if (multipartFile != null && !multipartFile.isEmpty()) {
            var fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            var savedUser = userService.save(user);
            var uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) {
                user.setPhoto(null);
            }
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return "redirect:/userManagement";
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable(name = "id") final Long id, final Model model,
                           final RedirectAttributes redirectAttributes) {
        logger.info("Into the editUser method, id is {}", id);
        try {
            var user = userService.get(id);
            model.addAttribute("userForm", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            model.addAttribute("listRoles", userService.listRoles());
            return "user/user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addAttribute("message", ex.getMessage());
        }
        return "redirect:/userManagement";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") final Long id,
                             final RedirectAttributes redirectAttributes) {
        logger.info("Into the deleteUser method, id is {}", id);
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/userManagement";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") final Long id,
                                          @PathVariable("status") final boolean enabled,
                                          final RedirectAttributes redirectAttributes) {
        logger.info("Into the updateUserEnabledStatus method, id is {}, status is {}", id, enabled);
        userService.updateUserEnabledStatus(id, enabled);
        var status = enabled ? "enabled" : "disabled";
        var message = "The user ID " + id + " has been " + status + " successfully";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/userManagement";
    }
}

