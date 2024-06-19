package it.cgmconsulting.myblog.configuration;

import it.cgmconsulting.myblog.entity.Consent;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.enumeration.Frequency;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.service.ConsentService;
import it.cgmconsulting.myblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SendNewsletter {

    private final ConsentService consentService;
    private final PostService postService;
    private final MailService mailService;

    //@Scheduled(fixedRate = 5000) //intervalli fissi espressi in millisecondi
    //@Scheduled(cron = "* * * * * *")

    @Scheduled(cron = "@weekly")
    public void sendNewsletterSched(){
        log.info("o-o-o-o-o-o-o-o-o-o-o-o-o-o-o");

        // recuperi i dati del consenso degli utenti per quelli che hanno frequency != NEVER
        // ed invii una mail con i post pubblicati
        // in base alla frequency e all`ultimo lastSent

        //Lista di tutti i consensi per la newsletter
        List<Consent> listaConsensi = consentService.findBySendNewsletterTrue();

        //Dividiamo per consenso mensile e settimanale in due liste nuove
        List<Consent> listaWeek = new ArrayList<>();
        List<Consent> listaMonth = new ArrayList<>();

        //Cicliamo la lista e suddividiamo
        for(Consent c : listaConsensi){
            if(c.getFrequency().equals(Frequency.WEEKLY))
                listaWeek.add(c);
            else
                listaMonth.add(c);
        }

        //prendiamo le liste dei post dell`ultima settimana e dell`ultimo mese
        List<Post> listaPostSettimanali = postService.getLastWeekPosts();
        List<Post> listaPostMensili = postService.getLastMonthPosts();

        //Utilizziamo lo StringBuilder per creare una stringa dinamica (contenuto della mail)
        StringBuilder weeklyMailContent = new StringBuilder();
        StringBuilder monthlyMailContent = new StringBuilder();

        for(Post p : listaPostSettimanali) {
            weeklyMailContent.append("<br> <a href='http://localhost:8090/api/v0/posts/")
                    .append(p.getId())
                    .append("'>")
                    .append(p.getTitle())
                    .append("</a>");
        }

        for(Post p : listaPostMensili) {
            monthlyMailContent.append("<br> <a href='http://localhost:8090/api/v0/posts/")
                    .append(p.getId())
                    .append("'>")
                    .append(p.getTitle())
                    .append("</a>");
        }

        if(!listaPostSettimanali.isEmpty()){
            for(Consent c : listaWeek){
                if(validWeeklyConsent(c)){
                    mailService.sendMail(mailService.createMail(c.getConsentId().getUserId(),
                            "MyBlog: ecco i post della settimana",
                            "Ciao " + c.getConsentId().getUserId().getUsername() + ", ecco i nuovi post di questa settimana: <br>" + String.valueOf(weeklyMailContent)));
                    c.setLastSent(LocalDate.now());
                    consentService.save(c);
                }
            }
        }

        if(!listaPostMensili.isEmpty()){
            for(Consent c : listaMonth){
                if(validMonthlyConsent(c)){
                    mailService.sendMail(mailService.createMail(c.getConsentId().getUserId(),
                            "MyBlog: ecco i post del mese",
                            "Ciao " + c.getConsentId().getUserId().getUsername() + ", ecco i nuovi post di questo mese: <br>" + String.valueOf(monthlyMailContent)));
                    c.setLastSent(LocalDate.now());
                    consentService.save(c);
                }
            }
        }

    }


    private boolean validWeeklyConsent(Consent c){
        return (c.getLastSent() == null
                || LocalDate.now().minusWeeks(1).isAfter(c.getLastSent().minusDays(1)))
                && c.getConsentId().getUserId().isEnabled();
    }

    private boolean validMonthlyConsent(Consent c){
        return (c.getLastSent() == null
                || LocalDate.now().minusMonths(1).isAfter(c.getLastSent().minusDays(1)))
                && c.getConsentId().getUserId().isEnabled();
    }

}
