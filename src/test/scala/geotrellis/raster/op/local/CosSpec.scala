package geotrellis.raster.op.local

import geotrellis._
import geotrellis.source._
import geotrellis.process._

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import geotrellis.testutil._

class CosSpec extends FunSpec
                with ShouldMatchers
                with TestServer
                with RasterBuilders {
  describe("Cos") {
    it("finds the cosine of a double raster") {
      val rasterData = Array (
        0.0,  1.0/6,  1.0/3,    1.0/2,  2.0/3,  5.0/6,
        1.0,  7.0/6,  4.0/3,    3.0/2,  5.0/3, 11.0/6,
        2.0, 13.0/6,  7.0/3,    5.0/2,  8.0/3, 17.0/6,

       -0.0, -1.0/6, -1.0/3,   -1.0/2, -2.0/3, -5.0/6,
       -1.0, -7.0/6, -4.0/3,   -3.0/2, -5.0/3,-11.0/6,
       -2.0,-13.0/6, -7.0/3,    Double.PositiveInfinity,  Double.NegativeInfinity, Double.NaN
      ).map(_ * math.Pi)
      val expected = rasterData.map(math.cos(_))
      val rs = createRasterSource(rasterData, 3, 3, 2, 2)
      run(rs.localCos) match {
        case Complete(result, success) =>
          for (y <- 0 until 6) {
            for (x <- 0 until 6) {
              val theCos = result.getDouble(x, y)
              val epsilon = math.ulp(theCos)
              val theExpected = expected(y*6 + x)
              if (x >= 3 && y == 5) {
                theCos.isNaN should be (true)
              } else {
                theCos should be (theExpected plusOrMinus epsilon)
              }
            }
          }
        case Error(msg, failure) =>
          println(msg)
          println(failure)
          assert(false)
      }
    }
    it("finds the cosine of an int raster") {
      val rasterData = Array (
        0, 1,   2, 3,
        4, 5,   6, 7,

       -4,-5,  -6,-7,
       -1,-2,  -3,NODATA
      )
      val expected = rasterData.map(math.cos(_))
                               .toList
                               .init
      val rs = createRasterSource(rasterData, 2, 2, 2, 2)
      run(rs.localCos) match {
        case Complete (result, success) =>
          for (y <- 0 until 4) {
            for (x <- 0 until 4) {
              val isLastValue = (x == 3 && y == 3)
              if (!isLastValue) {
                val theResult = result.getDouble(x, y)
                val expectedResult = expected(y*4 + x)
                val epsilon = math.ulp(theResult)
                theResult should be (expectedResult plusOrMinus epsilon)
              } else {
                result.getDouble(x, y).isNaN should be (true)
              }
            }
          }
        case Error (msg, failure) =>
          println(msg)
          println(failure)
          assert(false)
      }
    }
  }
  describe("Cos on Raster") {
    it("finds the cosine of a double raster") {
      val rasterData = Array (
        0.0,  1.0/6,  1.0/3,    1.0/2,  2.0/3,  5.0/6,
        1.0,  7.0/6,  4.0/3,    3.0/2,  5.0/3, 11.0/6,
        2.0, 13.0/6,  7.0/3,    5.0/2,  8.0/3, 17.0/6,

       -0.0, -1.0/6, -1.0/3,   -1.0/2, -2.0/3, -5.0/6,
       -1.0, -7.0/6, -4.0/3,   -3.0/2, -5.0/3,-11.0/6,
       -2.0,-13.0/6, -7.0/3,    Double.PositiveInfinity,  Double.NegativeInfinity, Double.NaN
      ).map(_ * math.Pi)
      val expected = rasterData.map(math.cos(_))
      val rs = createRaster(rasterData, 6, 6)
      val result = get(rs.localCos())
      for (y <- 0 until 6) {
        for (x <- 0 until 6) {
          val theCos = result.getDouble(x, y)
          val epsilon = math.ulp(theCos)
          val theExpected = expected(y*6 + x)
          if (x >= 3 && y == 5) {
            theCos.isNaN should be (true)
          } else {
            theCos should be (theExpected plusOrMinus epsilon)
          }
        }
      }
}
    it("finds the cosine of an int raster") {
      val rasterData = Array (
        0, 1,   2, 3,
        4, 5,   6, 7,

       -4,-5,  -6,-7,
       -1,-2,  -3,NODATA
      )
      val expected = rasterData.map(math.cos(_))
                               .toList
                               .init
      val rs = createRaster(rasterData, 4, 4)
      val result = get(rs.localCos())
      for (y <- 0 until 4) {
        for (x <- 0 until 4) {
          val isLastValue = (x == 3 && y == 3)
          if (!isLastValue) {
            val theResult = result.getDouble(x, y)
            val expectedResult = expected(y*4 + x)
            val epsilon = math.ulp(theResult)
            theResult should be (expectedResult plusOrMinus epsilon)
          } else {
            result.getDouble(x, y).isNaN should be (true)
          }
        }
      }
    }
  }
}
