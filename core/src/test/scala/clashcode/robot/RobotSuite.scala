package clashcode.robot

import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import java.io.{ObjectOutputStream, ByteArrayOutputStream}

class RobotSuite extends FeatureSpec with Matchers {

  feature("Robot") {

    scenario("should have 128 different situations") {
      128 should equal(Situations.all.size)
    }

    scenario("generations info should have sane binary size") {

      val parents = (0 to 20).map(id => RobotCode.createRandomCode("parent" + id))
      val robot = Robot(RobotCode(parents.head.code, "me", parents), 0)

      val bos = new ByteArrayOutputStream();
      val oos = new ObjectOutputStream(bos);
      oos.writeObject(robot);
      oos.close();

      1000 should be > bos.toByteArray.length
    }

    scenario("empty info should have sane binary size") {

      val robot = Robot(RobotCode.createRandomCode("parent"), 0)

      val bos = new ByteArrayOutputStream();
      val oos = new ObjectOutputStream(bos);
      oos.writeObject(robot);
      oos.close();

      566 should equal(bos.toByteArray.length)
    }

    scenario("should merge ancestor info") {
      val parent1 = RobotCode(Array.fill(128)(0.toByte), "this", Seq.empty)
      val parent2 = RobotCode(Array.fill(128)(0.toByte), "that", Seq(parent1))
      val child = RobotCode(Array.fill(128)(0.toByte), "me", Seq(parent1, parent2))
      val child2 = RobotCode(Array.fill(128)(0.toByte), "me", Seq(child, parent1, parent2))

      child.generations should equal(Map("me" -> 1, "that" -> 1, "this" -> 1))
      child2.generations should equal(Map("me" -> 2, "that" -> 1, "this" -> 1))
    }

  }
  
  feature("Item converter") {
    scenario("field with size 3 has one item at [2, 1]") {
      val items = List(false, false, false, false, false, true, false, false, false)
      val citems = ItemConverter.convertItems(items, 3)
      items.size should be (9)
      citems should be (List(FieldPos(2, 1))) 
    }
  }

}