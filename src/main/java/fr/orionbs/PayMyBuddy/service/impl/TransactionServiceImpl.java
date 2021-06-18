package fr.orionbs.PayMyBuddy.service.impl;

import fr.orionbs.PayMyBuddy.model.BankJournal;
import fr.orionbs.PayMyBuddy.model.Type;
import fr.orionbs.PayMyBuddy.model.User;
import fr.orionbs.PayMyBuddy.model.UserTransaction;
import fr.orionbs.PayMyBuddy.repository.BankJournalRepository;
import fr.orionbs.PayMyBuddy.repository.UserRepository;
import fr.orionbs.PayMyBuddy.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Transactional
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    BankJournalRepository bankJournalRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public void BankToUser(String emailUser, float amount) {
        log.info("Service BankToUser:");

        String dateNow = LocalDate.now().toString();

        log.info("Création de la transaction: BankJournal vers User: "+emailUser);
        UserTransaction userTransaction = UserTransaction
                .builder()
                .date(dateNow)
                .price(amount)
                .description("Compte Bancaire de "+emailUser+" vers compte PMB.")
                .sendTo(emailUser)
                .sendBy("BankJournal")
                .type(Type.WITHDRAWAL)
                .build();

        User user = userRepository.findByEmail(emailUser);
        user.getUserTransactions().add(userTransaction);
        user.setAmount(user.getAmount()+amount);

        log.info("Mise à jour User : Transaction: "+userTransaction+" Old Amount: "+user.getAmount()+" New Amount: "+(user.getAmount()+amount)+".");
        userRepository.save(user);

        BankJournal bankJournal = BankJournal
                .builder()
                .date(dateNow)
                .description("Compte Bancaire de "+emailUser+" vers compte PMB.")
                .who(emailUser)
                .amount(amount)
                .type(Type.WITHDRAWAL)
                .build();

        log.info("Enregistrement transaction dans le journal de banque : "+bankJournal+".");
        bankJournalRepository.save(bankJournal);
    }

    @Override
    public void UserToBank(String emailUser, float amount) {
        log.info("Service UserToBank:");

        String dateNow = LocalDate.now().toString();

        log.info("Création de la transaction: User: "+emailUser+" vers BankJournal.");
        UserTransaction userTransaction = UserTransaction
                .builder()
                .date(dateNow)
                .price(amount)
                .description("Compte PMB de "+emailUser+" vers Compte Bancaire.")
                .sendTo("BankJournal")
                .sendBy(emailUser)
                .build();

        User user = userRepository.findByEmail(emailUser);
        if (user.getAmount()<amount) {

        } else {
            user.setAmount(user.getAmount()-amount);
            user.getUserTransactions().add(userTransaction);
        }



        log.info("Mise à jour User : Transaction: "+userTransaction+" Old Amount: "+user.getAmount()+" New Amount: "+(user.getAmount()+amount)+".");
        userRepository.save(user);

        BankJournal bankJournal = BankJournal
                .builder()
                .date(dateNow)
                .description("Compte Bancaire de "+emailUser+" vers compte PMB.")
                .who(emailUser)
                .amount(amount)
                .type(Type.WITHDRAWAL)
                .build();

        log.info("Enregistrement transaction dans le journal de banque : "+bankJournal+".");
        bankJournalRepository.save(bankJournal);
    }

    @Override
    public void UserToUser(String emailUserSender, String emailUserCollector, float amount) {

    }
}