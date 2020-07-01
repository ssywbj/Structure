package com.suheng.structure.database;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.suheng.structure.database.bean.Person;
import com.suheng.structure.database.dao.PersonDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.suheng.structure.database.test", appContext.getPackageName());
    }

    @Test
    public void insertPerson() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.insert(new Person("覃以", 18));
        personDao.insert(new Person("苏恒", 5));
    }

    @Test
    public void select() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        List<Person> persons = personDao.select();
        Log.d("Wbj", "person size: " + persons.size());
        for (Person person : persons) {
            Log.d("Wbj", "person: " + person);
        }
    }

    @Test
    public void delete() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.delete(2);
    }

    @Test
    public void update() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.update("杰", 1);
    }

    @Test
    public void count() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        Log.d("Wbj", "count: " + personDao.count());
    }

    @Test
    public void insertContentValues() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.insertContentValues();
    }

    @Test
    public void deleteContentValues() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.deleteContentValues(3);
    }

    @Test
    public void updateContentValues() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        personDao.updateContentValues(5);
    }

    @Test
    public void selectContentValues() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PersonDao personDao = new PersonDao(appContext);
        List<Person> persons = personDao.selectContentValues();
        Log.d("Wbj", "person size: " + persons.size());
        for (Person person : persons) {
            Log.d("Wbj", "person: " + person);
        }
    }
}