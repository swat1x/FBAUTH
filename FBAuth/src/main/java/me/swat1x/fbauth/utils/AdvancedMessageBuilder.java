package me.swat1x.fbauth.utils;

import com.google.common.collect.Lists;
import lombok.Value;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdvancedMessageBuilder {

    private final List<MessagePart> partList = new ArrayList<>();

    public AdvancedMessageBuilder append(String text) {
        partList.add(new MessagePart(text, null, null, null));
        return this;
    }

    public AdvancedMessageBuilder append(String text, String hoverText) {
        partList.add(new MessagePart(text, Lists.newArrayList(hoverText), null, null));
        return this;
    }
    public AdvancedMessageBuilder append(String text, Collection<? extends String> hoverText) {
        partList.add(new MessagePart(text, hoverText, null, null));
        return this;
    }


    public AdvancedMessageBuilder append(String text, String hoverText, ClickEventType clickEventType, String clickValue) {
        partList.add(new MessagePart(text, Lists.newArrayList(hoverText), clickEventType, clickValue));
        return this;
    }
    public AdvancedMessageBuilder append(String text, Collection<? extends String> hoverText, ClickEventType clickEventType, String clickValue) {
        partList.add(new MessagePart(text, hoverText, clickEventType, clickValue));
        return this;
    }


    public AdvancedMessageBuilder append(String text, ClickEventType clickEventType, String clickValue) {
        partList.add(new MessagePart(text, null, clickEventType, clickValue));
        return this;
    }

    public void send(ProxiedPlayer player){
        player.sendMessage(build());
    }

    private TextComponent build = null;

    public TextComponent build(){
        if(build != null){
            return build;
        }
        TextComponent c = new TextComponent();
        partList.forEach( messagePart -> {
            c.addExtra(messagePart.get());
        });
        build = c;
        return c;
    }

    public TextComponent forceBuild(){
        TextComponent c = new TextComponent();
        partList.forEach( messagePart -> {
            c.addExtra(messagePart.get());
        });
        build = c;
        return c;
    }


    public static AdvancedMessageBuilder get() {
        return new AdvancedMessageBuilder();
    }

    @Value
    private static class MessagePart {

        String text;
        Collection<? extends String> hoverText;
        ClickEventType clickEvent;
        String clickEventValue;

        public TextComponent get() {
            TextComponent component = new TextComponent(text);
            if (hoverText != null) {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join("\n", hoverText)).create()));
            }
            if (clickEvent != null) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(clickEvent.name()), clickEventValue));
            }
            return component;
        }

    }

    public enum ClickEventType {

        SUGGEST_COMMAND,
        RUN_COMMAND,
        OPEN_URL;

    }


}
