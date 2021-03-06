package scalacode.service

import javacode.util.xml.{XStreamFactory, XStreamUtils}

import scalacode.dao.MessageProcessDao
import scalacode.entity._

/**
 * Created by Administrator on 2016/2/24.
 */
class WeixinMessageService {

  def processTextMessage(textMessage: TextMessage): String = {
    MessageProcessDao.saveTextMessage(textMessage)
    val responseMessage = new TextMessage
    responseMessage.Content = "thank you send message to me"
    responseMessage.FromUserName = textMessage.ToUserName
    responseMessage.ToUserName = textMessage.FromUserName
    responseMessage.MsgType = "text"
    responseMessage.CreateTime = System.currentTimeMillis() + ""
    val xml = XStreamFactory.getInstance(Array(classOf[TextMessage]))
    xml.toXml(responseMessage)
  }

  def processVideoMessage(videoMessage: VideoMessage): Unit = {

  }

  def processImageMessage(imageMessage: ImageMessage): Unit = {

  }

  def processLinkMessage(linkMessage: LinkMessage): Unit = {

  }

  def processPositionMessage(positionMessage: PositionMessage): Unit = {

  }

  def processVoiceMessage(voiceMessage: VoiceMessage): Unit = {

  }
}
