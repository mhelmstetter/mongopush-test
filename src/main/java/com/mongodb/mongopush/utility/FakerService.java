package com.mongodb.mongopush.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

@Component
public class FakerService {
	
	Faker faker = new Faker();
	Random random = new Random();
	
	public Date getRandomDate(int daysFromCurrentMin, int daysFromCurrentMax) {
        Calendar c = Calendar.getInstance();
        int daysToAdd = faker.random().nextInt(daysFromCurrentMin, daysFromCurrentMax);
        c.add(Calendar.DATE, daysToAdd);
        c.set(Calendar.HOUR, faker.random().nextInt(0, 23));
        c.set(Calendar.MINUTE, faker.random().nextInt(0, 59));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
	
	public Date getRandomDate()
	{
		Date date = new Date();
		date.setTime((long) (date.getTime() - Math.abs(Math.floor(random.nextGaussian() * 100000000 * 3000))));
		return date;
	}
	
	public String getRandomText()
	{
		return faker.book().title();
	}

}
