package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

class CardTest {
    Date date = new Date();

    void setLocalHost(){
        Configuration.holdBrowserOpen=true;
        open("http://localhost:9999");
    }

    // положительная проверка
    @Test
    void CardAlfaTesting_1(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Ульяновск");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));
        $("[data-test-id='name'] input").setValue("Иванов Петр");
        $("[data-test-id='phone'] input").setValue("+79278243700");
        $("[data-test-id='agreement']").click();

        $$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));

        String textOfSuccess = $(withText("Успешно!")).getText();
        assertEquals(textOfSuccess, "Успешно!");
    }

    // дата выставлена вручную
    @Test
    void CardAlfaTesting_2(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Санкт-Петербург");

        WebElement changeInput = $("[placeholder='Дата встречи']");
        changeInput.sendKeys(Keys.CONTROL + "A");
        changeInput.sendKeys(Keys.BACK_SPACE);

        Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 96));
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(tomorrow));

        $("[data-test-id='name'] input").setValue("Петр Иванов-Никитин");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        String textOfSuccess = $(withText("Успешно!")).getText();
        assertEquals(textOfSuccess, "Успешно!");
    }

    // дата на 3 недели вперед
    @Test
    void CardAlfaTesting_3(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Казань");
        WebElement changeInput = $("[placeholder='Дата встречи']");
        changeInput.sendKeys(Keys.CONTROL + "A");
        changeInput.sendKeys(Keys.BACK_SPACE);

        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        int noOfDays = 21; //i.e three weeks
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);

        $("[placeholder='Дата встречи']").setValue(dateFormat.format(calendar.getTime()));

        $("[data-test-id='name'] input").setValue("Марина Васильева");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        String textOfSuccess = $(".notification__title").getText();
        assertEquals(textOfSuccess, "Успешно!");
    }

    //город на латинице
    @Test
    void CardAlfaTesting_4(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Eaf");

        WebElement changeInput = $("[placeholder='Дата встречи']");
        changeInput.sendKeys(Keys.CONTROL + "A");
        changeInput.sendKeys(Keys.BACK_SPACE);

        Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 96));
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(tomorrow));

        $("[data-test-id='name'] input").setValue("Петр Иванов-Никитин");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Доставка в выбранный город недоступна");
    }

    //неверная дата
    @Test
    void CardAlfaTesting_5(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Москва");

        WebElement changeInput = $("[placeholder='Дата встречи']");
        changeInput.sendKeys(Keys.CONTROL + "A");
        changeInput.sendKeys(Keys.BACK_SPACE);

        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        int noOfDays = -14; // 2 weeks
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);

        $("[placeholder='Дата встречи']").setValue(dateFormat.format(calendar.getTime()));

        $("[data-test-id='name'] input").setValue("Петр Иванов");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Заказ на выбранную дату невозможен");
    }

    // имя на латинице
    @Test
    void CardAlfaTesting_6(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Москва");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));

        $("[data-test-id='name'] input").setValue("ivanov");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.");
    }

    // неверный телефон
    @Test
    void CardAlfaTesting_7(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Москва");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));

        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("879278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.");
    }

    // не стоит галочка на согласии
    @Test
    void CardAlfaTesting_8(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Москва");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));

        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $$("button").find(exactText("Забронировать")).click();

        String getClassesOfAgreement = $("[data-test-id='agreement']").getAttribute("class");
        assertEquals(getClassesOfAgreement, "checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid" );
    }

    // пустое поле города
    @Test
    void CardAlfaTesting_9(){
        setLocalHost();

        $("[placeholder='Город']").setValue("");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));

        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Поле обязательно для заполнения");
    }

    // нет даты
    @Test
    void CardAlfaTesting_10(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Новосибирск");
        WebElement changeInput = $("[placeholder='Дата встречи']");
        changeInput.sendKeys(Keys.CONTROL + "A");
        changeInput.sendKeys(Keys.BACK_SPACE);

        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Неверно введена дата");
    }

    // пустое поле имени
    @Test
    void CardAlfaTesting_11(){
        setLocalHost();

        $("[placeholder='Город']").setValue("Псков");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        $("[placeholder='Дата встречи']").setValue(formatter.format(date));

        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();

        String textOfFail = $(".input_invalid .input__sub").getText();
        assertEquals(textOfFail, "Поле обязательно для заполнения");
    }
}