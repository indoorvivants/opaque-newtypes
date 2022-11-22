/*
 * Copyright 2022 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opaque_newtypes

trait SameRuntimeType[A, T]:
  def apply(a: A): T

object SameRuntimeType:
  def apply[A, T](f: A => T): SameRuntimeType[A, T] =
    new:
      override def apply(a: A): T = f(a)

trait TotalWrapper[Newtype, Impl](using ev: Newtype =:= Impl):
  inline def raw(inline a: Newtype): Impl   = ev.apply(a)
  inline def apply(inline s: Impl): Newtype = ev.flip.apply(s)

  private val flipped = ev.flip

  given SameRuntimeType[Newtype, Impl] = new:
    override def apply(a: Newtype): Impl = ev.apply(a)

  given SameRuntimeType[Impl, Newtype] = new:
    override def apply(a: Impl): Newtype = flipped.apply(a)

  extension (a: Newtype)
    inline def value = raw(a)

    inline def into[X](inline other: TotalWrapper[X, Impl]): X =
      other.apply(raw(a))

    inline def map(inline f: Impl => Impl): Newtype = apply(f(raw(a)))
end TotalWrapper

inline given [A, T](using
    bts: SameRuntimeType[T, A],
    ord: Ordering[A]
): Ordering[T] =
  Ordering.by(bts.apply(_))

trait OpaqueString[A](using A =:= String) extends TotalWrapper[A, String]
trait OpaqueInt[A](using A =:= Int)       extends TotalWrapper[A, Int]
trait OpaqueLong[A](using A =:= Long)     extends TotalWrapper[A, Long]
trait OpaqueDouble[A](using A =:= Double) extends TotalWrapper[A, Double]
trait OpaqueFloat[A](using A =:= Float)   extends TotalWrapper[A, Float]

abstract class YesNo[A](using ev: Boolean =:= A):
  val Yes: A = ev.apply(true)
  val No: A  = ev.apply(false)

  given SameRuntimeType[A, Boolean] = SameRuntimeType(_ == Yes)
  given SameRuntimeType[Boolean, A] = SameRuntimeType(if _ then Yes else No)

  inline def apply(inline b: Boolean): A = ev.apply(b)

  extension (inline a: A)
    inline def value: Boolean        = a == Yes
    inline def flip: A               = if value then No else Yes
    inline def &&(inline other: A)   = a.value && other.value
    inline def `||`(inline other: A) = a.value || other.value
end YesNo
