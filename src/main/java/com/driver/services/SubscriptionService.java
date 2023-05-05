package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        int totalPrice = 0;
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
//       subscription.setStartSubscriptionDate(new Date());
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            totalPrice = 500 + (subscriptionEntryDto.getNoOfScreensRequired()*200);
        } else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            totalPrice = 800 + (subscriptionEntryDto.getNoOfScreensRequired()*250);
        }
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE)){
            totalPrice = 1000 + (subscriptionEntryDto.getNoOfScreensRequired()*350);
        }
        subscription.setTotalAmountPaid(totalPrice);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        return totalPrice;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        int prevPaid = user.getSubscription().getTotalAmountPaid();
        int currPaid = 0;
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)) {
            currPaid  = 800 + (250 * user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
        }
        else {
            currPaid  = 1000 + (350 * user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
        }
        subscriptionRepository.save(user.getSubscription());
        return currPaid - prevPaid;

    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> revenueList = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription find : revenueList){
            revenue += find.getTotalAmountPaid();
        }

        return revenue;
    }

}
