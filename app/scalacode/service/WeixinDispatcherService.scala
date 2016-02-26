package scalacode.service

import java.io.StringReader
import javacode.util.clazz.ClassUtils
import javacode.util.xml.JdomUtils

import org.jdom2.Element
import play.Logger

import scala.collection.mutable
import scalacode.dao.EventProcessDao
import scalacode.entity._
import scalacode.util.CheckUtils

/**
 * Created by Administrator on 2016/2/20.
 */
class WeixinDispatcherService extends BaseSerivce {

  lazy private val dispatcherCache = initDispatcherCache()

  private def initDispatcherCache(): mutable.HashMap[String, mutable.HashMap[String, TodoConfig]] = {
    val tempCache: mutable.HashMap[String, mutable.HashMap[String, TodoConfig]] = new mutable.HashMap[String, mutable.HashMap[String, TodoConfig]]()
    var key = ""
    if (WeixinConfigFactory.weixinConfig.getProcessConfig != null && WeixinConfigFactory.weixinConfig.processConfig.process != null && WeixinConfigFactory.weixinConfig.processConfig.process.length > 0) {
      for (process <- WeixinConfigFactory.weixinConfig.processConfig.process) {
        key = key + process.getMsgType
        val todos = process.todoConfig
        if (todos != null && todos.length > 0) {
          val tempMap = new mutable.HashMap[String, TodoConfig]();
          for (todo <- todos) {
            tempMap.put(todo.conditionValue, todo)
          }
          tempCache.put(key, tempMap)
        }
      }
    }
    Logger.info("tempCache = " + tempCache)
    tempCache
  }


  private def findTodo(root: Element, todoMap: mutable.HashMap[String, TodoConfig]): TodoConfig = {
    val todo = todoMap.find(p => p._1.equals(jdom.selectField(root, p._2.getConditionField)))
    if (todo != None)
      todo.get._2
    else
      null
  }


  private def matchTodoConfig(root: Element, msgType: String): TodoConfig = {
    if (dispatcherCache.contains(msgType)) {
      val todoMap = dispatcherCache.get(msgType).get
      findTodo(root, todoMap)
    } else {
      null
    }
  }

  private def processTodo(root: Element, todoConfig: TodoConfig) = {
    Logger.info("processTodo ----todoConfig = " + todoConfig)
    if (todoConfig != null) {
      val className = todoConfig.getEntityClass
      val entity = jdom.selectFields(root, Class.forName(className))
      Logger.info("parser entity is =" + entity)
      val clazz = Class.forName(todoConfig.getProcessClass)
      val method = ClassUtils.getMethod(todoConfig.getProcessMethod, clazz, Class.forName(className))
      method.invoke(clazz.newInstance(), entity.asInstanceOf[Object])
    }
  }

  def dispatchMessage(xmlContent: String): Unit = {
    val root = jdom.getRootElement(new StringReader(xmlContent))
    val msgType = jdom.selectField(root, WeixinConstants.MSG_TYPE_NAME)
    Logger.info("processWeixinMessage msgType=" + msgType)
    val todoConfig = matchTodoConfig(root, msgType)
    if (todoConfig != null) {
      processTodo(root, todoConfig)
    }
  }
}