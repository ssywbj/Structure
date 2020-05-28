package com.suheng.structure.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suheng.structure.gson.bean.Result;
import com.suheng.structure.gson.bean.ResultList;
import com.suheng.structure.gson.bean.ResultUser;
import com.suheng.structure.gson.bean.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private User user = new User(0x11, 18, "Wbj@qq.com");

    //{...}：使用大括号括起来，表示该json串能解析成对象
    private String json = "{" + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}";
    //[...]：使用方括号括起来，表示该json串能解析成数组或列表
    private String jsonArray = "[" + "\"JAVA\"" + ",\"PHP\"" + ",\"C++\"" + "]";

    private String jsonUser = "{" + "\"code\":-1" + ",\"msg\":ok" + ",data:{" + "\"memberId\":17" + ",\"age\":18" + ",\"email_address\":\"Wbj@qq.com\"" + "}" + "}";
    private String jsonList = "{" + "\"code\":-1" + ",\"msg\":ok" + ",data:[" + "\"JAVA\"" + ",\"PHP\"" + ",\"C++\"" + "]" + "}";

    private Gson mGson;

    @Before
    public void before() {
        System.out.println("-------------------开始测试-------------------, = " + (2753.47-875.8));
        mGson = new Gson();
    }

    @After
    public void after() {
        System.out.println("-------------------结束测试-------------------");
    }

    /**
     * 基本数据类型的生成及解析。toJson：生成json字符串，序列化(对象变字符串)；fromJson：解析json字串符，反序列化(字符串变对象)。
     */
    @Test
    public void testBasic() {
        String jsonInt = mGson.toJson(100);
        String jsonDle = mGson.toJson(99.99);
        String jsonFlt = mGson.toJson(99.99f);
        String jsonBool = mGson.toJson(false);
        System.out.println("jsonInt = " + jsonInt + ", jsonDle = " + jsonDle
                + ", jsonFlt = " + jsonFlt + ", jsonBool = " + jsonBool);

        int i = mGson.fromJson("100", int.class);
        double dle = mGson.fromJson("99.99", double.class);
        float flt = mGson.fromJson("\"99.99\"", float.class);
        String str = mGson.fromJson("test", String.class);
        System.out.println("int = " + i + ", double = " + dle + ", float = " + flt + ", str = " + str);
    }

    @Test
    public void testFloatF() {
        float actual = mGson.fromJson("99.99f", float.class);
        assertEquals(99.99f, actual, 0.01f);
    }

    @Test(expected = NumberFormatException.class)
    public void testFloatA() {
        float actual = mGson.fromJson("99.99a", float.class);
        assertEquals(99.99f, actual, 0.01f);
    }

    @Test
    public void testToJson() {
        String actual = mGson.toJson(user);
        assertEquals(json, actual);
        System.out.println("json actual: " + actual);
    }

    @Test
    public void testPOJO() {
        User actual = mGson.fromJson(json, User.class);
        assertEquals(user, actual);
        System.out.println("object actual: " + actual + ", object expected: " + user);
    }

    @Test
    public void testSerializedName() {
        /*反序列化时，json串中 的"email_address"、"_email_address"、"EmailAddress"
        均可被映射成"emailAddress"属性，当多种情况同时出时，以最后一个出现的值为准*/
        User user = mGson.fromJson(json, User.class);//email_address
        json = "{\"memberId\":17" + ",\"age\":18" + ",\"_email_address\":\"Wbj@qq.com\"" + "}";//_email_address
        User actual = mGson.fromJson(json, User.class);
        assertEquals(user, actual);

        json = "{\"memberId\":17" + ",\"age\":18" + ",\"EmailAddress\":\"Wbj@qq.com\"" + "}";//EmailAddress
        User expected = mGson.fromJson(json, User.class);
        assertEquals(expected, actual);
        System.out.println("SerializedName: " + expected);
    }

    @Test
    public void testSerializedNameAlternate() {
        json = "{\"memberId\":17" + ",\"age\":18" + ",\"_email_address\":\"aaa@qq.com\"" + ",\"email_address\":\"bbb@qq.com\""
                + ",\"EmailAddress\":\"ccc@qq.com\"" + "}";//当多种情况同时出时，以最后一个出现的值为准
        User actual = mGson.fromJson(json, User.class);

        json = "{\"memberId\":17" + ",\"age\":18" + ",\"EmailAddress\":\"aaa@qq.com\"" + ",\"email_address\":\"bbb@qq.com\""
                + ",\"_email_address\":\"ccc@qq.com\"" + "}";//当多种情况同时出时，以最后一个出现的值为准
        User expected = mGson.fromJson(json, User.class);
        assertEquals(expected, actual);
        System.out.println("SerializedNameAlternate: " + expected);

        json = "{\"memberId\":17" + ",\"age\":18" + ",\"EmailAddress\":\"aaa@qq.com\"" + ",\"email_address\":\"bbb@qq.com\""
                + ",\"_email_address\":\"ddd@qq.com\"" + "}";//当多种情况同时出时，以最后一个出现的值为准
        expected = mGson.fromJson(json, User.class);
        assertNotEquals(expected, actual);
        System.out.println("SerializedNameAlternate: " + expected);
    }

    @Test
    public void testArrayAndList() {
        //反向测试使用方括号[]括起来的json串可解析成数组或列表，为以下的testGenericStrings()做铺垫
        String[] array = {"JAVA", "PHP", "C++"};
        String actual = mGson.toJson(array);
        assertEquals(jsonArray, actual);
        System.out.println("actual: " + actual);

        List<String> list = new ArrayList<>();
        list.add("JAVA");
        list.add("PHP");
        list.add("C++");
        actual = mGson.toJson(list);
        assertEquals(jsonArray, actual);
        System.out.println("actual: " + actual);
    }

    @Test
    public void testGenericStrings() {
        String[] strings = mGson.fromJson(jsonArray, String[].class);//解析成数组
        System.out.println("strings: " + Arrays.toString(strings));

        /*当能解析成数组或列表时，一般选择列表，因为能方便地进行增、删等操作。但将上面代码中的String[].class直接改为List<String>.class
        是不行的，因为对于Java来说，List<String>、List<User>或List<Map<Object,Object>>等这些类型的字节码文件只有一个，
        那就是List.class，所以它无法判断要解析成哪一种类型，这也是Java泛型使用时要注意的问题————泛型擦除。*/
        //String[] list = mGson.fromJson(jsonArray, List<String>.class);//此操作不可行
        //为了解决泛型擦除问题，Gson提供TypeToken来实现对泛型的支持，如果要将以上的数据解析为List<String>时需要这样写：
        List<String> list = mGson.fromJson(jsonArray, new TypeToken<List<String>>() {
        }.getType());//解析成列表
        /*因为TypeToken类构造方法用protected修饰，所以使用时要写成"new TypeToken<List<String>>(){}.getType()"
        而不是"new TypeToken<List<String>>().getType()*/
        System.out.println("   list: " + list);
    }

    @Test
    public void testGenericObject() {
        ResultUser resultUser = mGson.fromJson(jsonUser, ResultUser.class);
        ResultList resultList = mGson.fromJson(jsonList, ResultList.class);

        /*泛型解析对POJO的设计影响：可以减少无关的代码。如以下解析过程：只需要传入已有的User和List<String>
        可以实现解析，不再需要另外新建ResultUser类和ResultList类*/
        Result<User> genericUser = mGson.fromJson(jsonUser, new TypeToken<Result<User>>() {
        }.getType());
        assertEquals(resultUser.getData(), genericUser.getData());
        System.out.println("genericUser: " + genericUser);

        Result<List<String>> genericList = mGson.fromJson(jsonList, new TypeToken<Result<List<String>>>() {
        }.getType());
        assertEquals(resultList.getData(), genericList.getData());
        System.out.println("genericList: " + genericList);
    }

    @Test
    public void testGenericIgnore() {
        /*用Result和Result<User>去接收Result<List<String>>都可以，这也是泛型擦除导致的结果。因为不管是Result还是Result<User>，
        在Java字节码文件里都是Result.class，泛型里的具体对象由我们传进去的内容决定*/
        Result expected = mGson.fromJson(jsonList, new TypeToken<Result<List<String>>>() {
        }.getType());//泛型擦除
        Result<User> actual = mGson.fromJson(jsonList, new TypeToken<Result<List<String>>>() {
        }.getType());//泛型擦除，Result<User>等价Result
        assertEquals(expected.getData(), actual.getData());
        System.out.println("expected: " + expected + ", actual: " + actual);

        //泛型擦除，Result<User>和Result<List<String>>均可接收Result<User>的解析内容，原因同上
        Result<User> userExpected = mGson.fromJson(jsonUser, new TypeToken<Result<User>>() {
        }.getType());
        Result<List<String>> userActual = mGson.fromJson(jsonUser, new TypeToken<Result<User>>() {
        }.getType());
        assertEquals(userExpected.getData(), userActual.getData());//因为我们重写了User的equals()，当id、age和emailAddress的值都一样时，我们就认为他们的内容一样
        System.out.println("userExpected: " + userExpected + "\n  userActual: " + userActual);
    }
}