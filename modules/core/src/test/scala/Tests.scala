import opaque_newtypes.*

object types:
  opaque type GameId = String
  object GameId extends OpaqueString[GameId]

  opaque type PlayerId = String
  object PlayerId extends OpaqueString[PlayerId]

  opaque type Counter = Int
  object Counter extends OpaqueInt[Counter]

  opaque type Balance = Int
  object Balance extends OpaqueInt[Balance]

  opaque type IsRigged = Boolean
  object IsRigged extends YesNo[IsRigged]

  opaque type Weight = Double
  object Weight extends OpaqueDouble[Weight]

  opaque type WeightF = Float
  object WeightF extends OpaqueFloat[WeightF]
end types

class Tests extends munit.FunSuite:
  import types.*

  val gid = GameId("hello")
  val cnt = Counter(25)
  val wt  = Weight(25.0)
  val wtf = WeightF(12.0f)

  test("value") {
    assertEquals(gid.value, "hello")
    assertEquals(cnt.value, 25)
    assertEquals(wt.value, 25.0)
    assertEquals(wtf.value, 12.0f)

    assertEquals(IsRigged.Yes.value, true)
    assertEquals(IsRigged.No.value, false)
    assertNotEquals(IsRigged.Yes, IsRigged.No)
  }

  test("into") {
    assertEquals(gid.into(PlayerId), PlayerId(gid.value))
    assertEquals(cnt.into(Balance), Balance(cnt.value))
  }

  test("map") {
    assertEquals(gid.map(_ + "!").value, "hello!")
    assertEquals(cnt.map(_ * 2).value, 50)
  }

  test("runtime") {
    assert(gid.isInstanceOf[String])
    assert(cnt.isInstanceOf[Int])
    assert(IsRigged.Yes.isInstanceOf[Boolean])
    assert(IsRigged.No.isInstanceOf[Boolean])

    def genericBool[T](v: T) =
      assert(v.isInstanceOf[Boolean])

    genericBool[IsRigged](IsRigged.Yes)
    genericBool[IsRigged](IsRigged.No)

    def genericInt[T](v: T) =
      assert(v.isInstanceOf[Int])

    genericInt(cnt)

    def genericString[T](v: T) =
      assert(v.isInstanceOf[String])

    genericString(gid)
  }

  test("using SameRuntimeType to derive typeclass instances for newtypes") {
    trait ByteSize[T]:
      def sizeof(t: T): Int

    object ByteSize:
      given ByteSize[Int]     = _ => 8
      given ByteSize[Byte]    = _ => 1
      given ByteSize[Boolean] = _ => 1
      given ByteSize[String]  = _.getBytes().size

    given deriveByteSize[Newtype, Runtime](using
        forth: SameRuntimeType[Newtype, Runtime],
        raw: ByteSize[Runtime]
    ): ByteSize[Newtype] = new:
      override def sizeof(t: Newtype): Int = raw.sizeof(forth.apply(t))

    assertEquals(
      summon[ByteSize[GameId]].sizeof(gid),
      summon[ByteSize[String]].sizeof("hello")
    )

    assertEquals(
      summon[ByteSize[IsRigged]].sizeof(IsRigged.Yes),
      summon[ByteSize[Boolean]].sizeof(true)
    )

  }
end Tests
