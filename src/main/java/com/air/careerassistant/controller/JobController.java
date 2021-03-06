package com.air.careerassistant.controller;

import com.air.careerassistant.model.job.Job;
import com.air.careerassistant.model.job.JobRepository;

import com.air.careerassistant.model.jobTrack.JobStatus;
import com.air.careerassistant.model.jobTrack.JobStatusRepository;
import com.air.careerassistant.model.post.Post;
import com.air.careerassistant.model.user.ApplicationUser;
import com.air.careerassistant.model.user.ApplicationUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Controller
public class JobController {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    ApplicationUserRepository applicationUserRepository;


    @GetMapping("/addjob")
    public String showDetails(Principal principal, Model m) {
        if (principal != null){
            m.addAttribute("principal",principal);
        }
        return "addjob";
    }

    @PostMapping("/jobsearchresult")
    public RedirectView saveDetails(
            Principal principal,
            String url,
            String company,
            String company_url,
            String title,
            String location,
            String description,
            String type
    ) {

        JobStatus newJobStatus = new JobStatus();
        jobStatusRepository.save(newJobStatus);
       
        ApplicationUser currentUser = applicationUserRepository.findByUsername(principal.getName());
        Job newJob = new Job(currentUser, url, company, company_url, title, location, description, type, newJobStatus);
        jobRepository.save(newJob);
        return new RedirectView("/allmyjobs");
    }

    @GetMapping("/jobdetails/{localId}")
    public String showNewJobDetails(Model model, Principal principal, @PathVariable Long localId) {
        model.addAttribute("principal",principal);
        Job job = jobRepository.getOne(localId);
        if (job.getApplicationUser().getUsername().equals(principal.getName())) {
            model.addAttribute("currentJob", job);
            model.addAttribute("posts", job.getPostList());
            model.addAttribute("user", job.getApplicationUser());
        }
//        System.out.println("This is the last updated" +job.getLastUpdated());
//        Instant tempDate = job.getLastUpdated().toInstant();
//        Instant today = Calendar.getInstance().getTime().toInstant();
//        System.out.println("This is Jack's crazy line " + ChronoUnit.DAYS.between(tempDate, today));
        return "details";
    }


    @PostMapping("/delete/job")
    public RedirectView deleteJob(Long jobId, Principal principal, String name) {
        if (name.equals(principal.getName())) {
            jobRepository.deleteById(jobId);
            return new RedirectView("/allmyjobs");
        }
        Job job = jobRepository.getOne(jobId);
        return new RedirectView("/jobdetails/" + jobId);
    }

}
