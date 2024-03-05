package project_1

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object main{

  def sha256Hash(text: String) : String = String.format("%064x", new java.math.BigInteger(1, java.security.MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))

  def main(args: Array[String]) {
    val startTimeMillis = System.currentTimeMillis()

    if(args.length != 3)
    {
      println("Usage: project_1 string difficulty #trials")
      sys.exit(1)
    }
    if(!args(1).matches("[0-9]*"))
    {
      println("The difficulty has to be between 1 and 10.");
      sys.exit(1)
    }
    if(args(1).toInt < 1 || args(1).toInt > 10)
    {
      println("The difficulty has to be between 1 and 10.");
      sys.exit(1)
    }
    val difficulty = args(1).toInt;

    if(!args(2).matches("[0-9]*"))
    {
      println("The number of trials has to be a number");
      sys.exit(1)
    }
    if(args(2).toInt < 0)
    {
      println("The number of trials has to be positive.");
      sys.exit(1)
    }
    val trials = args(2).toInt;

//    val random = if(args.length == 4 && args(3)=="random") 1 else 0

    val conf = new SparkConf().setAppName("Project_1")
    val sc = new SparkContext(conf)

    val header_1 = args(0)
      //"this_is_a_bitcoin_block_of_xxxx_and_yyyy"

    val zeroes_prefix = "0"*difficulty;

    val seed = new java.util.Date().hashCode;

    val nonce = sc.range(0, trials).mapPartitionsWithIndex((indx, iter) => {
      val rand = new scala.util.Random(indx + seed)
      iter.map(x => rand.nextInt(Int.MaxValue - 1) + 1)
    })

    val hash_result = nonce.map(x => (x.toString(), sha256Hash(x.toString()  + header_1 )))

    val ans = hash_result.filter( { case (x: String,y: String)  => y.startsWith(zeroes_prefix) } )
    ans.cache()

    println("==================================")
    if(ans.count!=0)
    {
      println("found. count:"+ans.count)
      val out = ans.take(1)(0)
      println("("+out._1+header_1+","+ out._2+")")
    }
    else
      println("did not find")
    val endTimeMillis = System.currentTimeMillis()
    val durationSeconds = (endTimeMillis - startTimeMillis) / 1000
    println("Time elapsed:" + durationSeconds + "s")
    println("==================================")
  }
}

