import com.mongodb.casbah.Imports._
import com.mongodb.casbah.query.dsl._
import collection.mutable.Stack
import org.scalatest._

class MongoIdSpec extends FlatSpec with BeforeAndAfter {

    // This should all come from a configuration source
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("test")
    val collection1 = db("testCollection1")
    val collection2 = db("testCollection2")
    val collection3 = db("testCollection3")

    before {
        // mongo actually allows you to create an _id of any string value,
        // but org.bson.types.ObjectId does not...
        val strId1 = "52406f9540f3bf7ed6b76c80"
        val oid1 = new org.bson.types.ObjectId(strId1)
        val obj1 = MongoDBObject("_id" -> oid1, "foo" -> 1)

        collection1.drop
        collection1 += obj1

        val strId2 = "52406f9540f3bf7ed6b76c81"
        val oid2 = new org.bson.types.ObjectId(strId2)
        val obj2 = MongoDBObject("_id" -> oid2, "bar" -> 1)

        collection1 += obj2

        val obj3 = MongoDBObject("name" -> "Indirect Reference",
                                 "other" -> MongoDBList(oid1, oid2))

        collection2.drop
        collection2 += obj3

        val obj4 = MongoDBObject("name" -> "Robert",
                                 "address" -> "Massachusetts",
                                 "phone" -> "1234567")

        val obj5 = MongoDBObject("name" -> "Satish",
                                 "address" -> "Pennsylvania",
                                 "phone" -> "8901234")

        collection3.drop
        collection3 += obj4
        collection3 += obj5
    }

    "A query on Collection " should "return a valid result" in {
         val q = MongoDBObject("foo" -> 1)
         val whatTypeAreyou = collection1.findOne(q)
         println(s"whatTypeAreyou : ${whatTypeAreyou.getClass}")
         val item:DBObject = collection1.findOne(q).get
         println(s"what are you: ${item.getClass}")
         val id = item.get("_id")
         val foo = item.get("foo")
         assert(foo.toString == "1")
    }

    "A projection " should "restrict fields in the result" in {
         val q = MongoDBObject()
         val p = MongoDBObject("address" -> 1, "_id" -> 0)
         val item: MongoCursor = collection3.find(q, p)

         println(item.getClass)
         val l = for {
             i <- item  // BasicDBObject
             j <- i     // key value Pair
         } yield j._2

         val results = l.toList
         assert(results.contains("Pennsylvania"))
         assert(results.contains("Massachusetts"))
         assert(!(results.contains("Robert")))
    }

    "A query by array entry on Collection " should "return a valid result" in {
         val q = MongoDBObject("name" -> "Indirect Reference")
         val p = MongoDBObject("other" -> 1, "_id" -> 0)

         val o = collection2.findOne(q, p).get
         val a = o.get("other").asInstanceOf[BasicDBList]

        //loop through the array to find each object by id ref
         for(idRef <- a.toArray) {
            val q1 = MongoDBObject("_id" -> idRef)            
            val res = collection1.findOne(q1)   
            assert(res.get("_id") == idRef)
         }

        // alternatively query using DSL "$in" operartor
        //find items in collection that match any "_id" in the given object list
         val q2 = "_id" $in a
         val res2 = collection1.find(q2)
         assert(res2.length == 2)

    }

    "A query with id on Collection" should "return a valid result" in {
         val q = MongoDBObject("foo" -> 1)
         val item = collection1.findOne(q).get
         val id = item.get("_id")

         val q2 = MongoDBObject("_id" -> id)
         val item2 = collection1.findOne(q2).get
         val id2 = item2.get("_id")
         val foo2 = item2.get("foo")
         assert(foo2.toString == "1")
         assert(id2 == id)
    }

    "A query with new id object on Collection" should "return a valid result" in {
        //converting from String to objectId
         val q = MongoDBObject("foo" -> 1)
         val item = collection1.findOne(q).get
         val id = item.get("_id")
         val ref = id.toString

         val q2 = MongoDBObject("_id" -> new org.bson.types.ObjectId(ref))
         val item2 = collection1.findOne(q2).get
         val id2 = item2.get("_id")
         val foo2 = item2.get("foo")
         assert(foo2.toString == "1")
         assert(id2 == id)
    }


    "All objects in a mongo collection " should " be added to a List" in {
      val q = MongoDBObject()
      val p = MongoDBObject("address" -> 1)
      val item: MongoCursor = collection3.find(q, p)

      val result = item.map(x => x.get("_id").toString).toList
      println(result)
      for(x <- result) {
        println(x)
        println(x.getClass)
      }
      assert(result.size == 2)

    }

    "A insert of a document" should "add empty array element" in {
        val obj1 = MongoDBObject("foo" -> "hi I am foo", "results" -> MongoDBList())
        println(s"before insert $obj1")
        
        collection1.insert(obj1)
        println(s"after insert $obj1")
        
        assert(obj1.get("_id").toString().equals("") == false)
        val list = obj1.as[MongoDBList]("results").toList
        assert(list.size == 0)

    }

    "A update of a document with array" should "add an item to the array" in {

        val obj = MongoDBObject("foo" -> "hi I am foo", "results" -> MongoDBList())
        collection1.drop()
        collection1.insert(obj)

        val query = MongoDBObject("foo" -> "hi I am foo")
        val doc1 = $push("results" -> "1")
        collection1.update(query, doc1)

        val obj1 = collection1.findOne().get
        println(s"obj1: $obj1")
        val list1 = obj1.get("results").asInstanceOf[BasicDBList]
        println(s"list1 : $list1")
        assert(list1.size == 1)

        import scala.language.reflectiveCalls
        val doc2 = $push("results") $each ("2", "3", "4")
        collection1.update(query, doc2)
        val obj2 = collection1.findOne().get
        val list2 = obj2.get("results").asInstanceOf[BasicDBList]
        println(s"list2 : $list2")
        assert(list2.size == 4)

    }
}