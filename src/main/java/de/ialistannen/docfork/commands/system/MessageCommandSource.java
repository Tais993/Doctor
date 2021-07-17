package de.ialistannen.docfork.commands.system;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class MessageCommandSource implements CommandSource {

  private final Message message;

  public MessageCommandSource(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  public MessageChannel getChannel() {
    return message.getChannel();
  }

  @Override
  public String rawText() {
    return message.getContentRaw();
  }

  @Override
  public RestAction<?> reply(Message message) {
    return getChannel().sendMessage(message);
  }

  @Override
  public RestAction<?> editOrReply(Message message) {
    return reply(message);
  }
}
