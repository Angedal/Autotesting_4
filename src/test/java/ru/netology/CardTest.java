package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

class CardTest {

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen=true;
        open("http://localhost:9999");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    }

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    String planningDate;

    // положительная проверка
    @Test
    void shouldTestWithCorrectFields(){

        planningDate = generateDate(5);
        $("[data-test-id='city'] input").setValue("Ульяновск");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов Петр");
        $("[data-test-id='phone'] input").setValue("+79278243700");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

        $("[data-test-id='notification'] .notification__title").shouldHave(text("Успешно!"));
    }

    // дата выставлена вручную и есть дефисы d названии города и фамилии
    @Test
    void shouldTestWithManualDateAndHyphenInName(){

        planningDate = generateDate(10);
        $("[data-test-id='city'] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Петр Иванов-Никитин");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

        $("[data-test-id='notification'] .notification__title").shouldHave(text("Успешно!"));
    }

    // дата на 3 недели вперед
    @Test
    void shouldTestWithDateAfterThreeWeeks(){

        planningDate = generateDate(21);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Марина Васильева");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

        $("[data-test-id='notification'] .notification__title").shouldHave(text("Успешно!"));
    }

    //город на латинице
    @Test
    void shouldTestWithWrongCity(){

        planningDate = generateDate(6);
        $("[data-test-id='city'] input").setValue("Eaf");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Петр Иванов-Никитин");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(text("Доставка в выбранный город недоступна"));
    }

    //неверная дата
    @Test
    void shouldTestWithWrongDate(){

        planningDate = generateDate(-5);
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Петр Иванов");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    // имя на латинице
    @Test
    void shouldTestWithWrongName(){

        planningDate = generateDate(7);
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("ivanov");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub")
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    // неверный телефон
    @Test
    void shouldTestWithWrongPhone(){

        planningDate = generateDate(20);
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("879278243");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    // нет согласия
    @Test
    void shouldTestWithoutAgreement(){

        planningDate = generateDate(9);
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='agreement']").shouldHave(cssClass("input_invalid"));
    }

    // нет города
    @Test
    void shouldTestWithoutCity(){

        planningDate = generateDate(9);
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    // нет даты
    @Test
    void shouldTestWithoutDate(){

        $("[data-test-id='city'] input").setValue("Новосибирск");
        $("[data-test-id='name'] input").setValue("Иванов иван");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input_invalid .input__sub").shouldHave(text("Неверно введена дата"));
    }

    // нет имени
    @Test
    void shouldTestWithoutName(){

        planningDate = generateDate(12);
        $("[data-test-id='city'] input").setValue("Псков");
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("+79278243600");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));;
    }
}