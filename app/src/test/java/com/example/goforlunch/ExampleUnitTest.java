package com.example.goforlunch;

import android.util.Log;

import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.goforlunch.WorkerAdapter.decidedFirstList;
import static com.example.goforlunch.repository.NetworkRepository.computeRatio;
import static com.example.goforlunch.repository.NetworkRepository.ratioRestaurant;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void compute_ratio_is_correct() {
        assertEquals(2, computeRatio(0.7, 1.5));
        assertEquals(3, computeRatio(0.8, 4.8));
        assertEquals(1, computeRatio(0.4, 2.7));
        assertEquals(0, computeRatio(0.1, 1.1));
    }

    @Test
    public void ratioRestaurant_is_correct() {
        List<Restaurant> restaurants = new ArrayList<Restaurant>() {{
            add(new Restaurant(Arrays.asList("1", "2"), "1234"));
            add(new Restaurant(Arrays.asList("1", "2", "3", "4"), "456"));
            add(new Restaurant(Arrays.asList("1", "2", "3", "4", "5", "6", "7"), "7894"));
            add(new Restaurant(new ArrayList<>(), "421"));
        }};

        assertEquals(0, ratioRestaurant("2159", restaurants));
        assertEquals(2, ratioRestaurant("1234", restaurants));
        assertEquals(4, ratioRestaurant("456", restaurants));
        assertEquals(7, ratioRestaurant("7894", restaurants));
        assertEquals(0, ratioRestaurant("421", restaurants));
    }

    @Test
    public void decidedFirstList_is_correct() {
        List<User> users = new ArrayList<User>() {{
            add(new User("Alphonse Daudet", "", ""));
            add(new User("Albert Einstein", "", ""));
            add(new User("Celestine Ernest", "", ""));
            add(new User("Ludivine Zaccari", "", ""));
            add(new User("Leonard Vinci", "", ""));
            add(new User("Sophie Germain", "", ""));
            add(new User("George Antonnin", "", ""));
        }};
        users.get(0).setRestaurantId("1234");
        users.get(2).setRestaurantId("1254");
        users.get(3).setRestaurantId("1547");
        users.get(6).setRestaurantId("5412");

        List<User> resultTest = decidedFirstList(users);

        assertEquals(users.get(6),resultTest.get(0));
        assertEquals(users.get(0),resultTest.get(1));
        assertEquals(users.get(2),resultTest.get(2));
        assertEquals(users.get(3),resultTest.get(3));
        assertEquals(users.get(1),resultTest.get(4));
        assertEquals(users.get(5),resultTest.get(5));
        assertEquals(users.get(4),resultTest.get(6));

        }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

}