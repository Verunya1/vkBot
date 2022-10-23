import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.model.events.messages.MessageNew;
import com.vk.api.sdk.objects.messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.objects.messages.KeyboardButton;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import com.vk.api.sdk.objects.messages.KeyboardButtonAction;
import com.vk.api.sdk.objects.messages.TemplateActionTypeNames;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;


public class VkBot  {
    public static void main(String[] args) throws ClientException,ApiException,InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient); //создали траспортный клиент чз которого будем передавать запросы
        Random random = new Random(); // нужен для отправки сообщений
        Keyboard keyboard = new Keyboard();

        List<List<KeyboardButton>> allKey = new ArrayList<>();
        List<KeyboardButton> line1 = new ArrayList<>();//ряд на котором мы будем добавлять кнопки
        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Привет").setType(TemplateActionTypeNames.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Какое у меня завтра расписание").setType(TemplateActionTypeNames.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
        allKey.add(line1);
        keyboard.setButtons(allKey);

        GroupActor actor =  new GroupActor(216652037,"vk1.a.EqEFh352n10fl6bP8lO5tQWi-AjSfu4nnywOY8VJTxZLEvWZEK474QqGj6t61ELeT5e2yPEjX2wzPN44vutMqfWoruuAc_YB14_QYFTvfF1mzTqAm0JgkUP5JuJvY6WYTxJl76vXCBNBq_vywUsJXf4MRf8VigtgDRDX-b11zkv06SD1kjkeQk7gS3AT0MIEZEr5dXchiT7QKR2Y9jBUEg"); //   в них передаем id и token
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();//ts - индификатор с которого надо начинать обрабатывать сообщения
        //для работы с long poll запросами

        while (true){ //цикл для бесконечного обращения vk api
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts); // получаем историю наших long pool запросов
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if(!messages.isEmpty()){
                messages.forEach(message -> {
                    System.out.println(message.toString());
                    try {
                        if(message.getText().equals("Привет")){
                            vk.messages().send(actor).message("Привет!").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                        else if(message.getText().equals("Кнопки")){
                            vk.messages().send(actor).message("Вот они").userId(message.getFromId()).randomId(random.nextInt(10000)).keyboard(keyboard).execute();
                        }
                        else if(message.getText().equals("Какое у меня завтра расписание")){
                            vk.messages().send(actor).message("А я откуда должен знать?").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                        else{
                            vk.messages().send(actor).message("Не понял, а ты вообще кто?").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                    }
                    catch (ApiException | ClientException e) {
                        e.printStackTrace();
                    }
                });
            }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
            }
    }


}
