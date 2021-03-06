/*
 * Tranquility.
 * Copyright (C) 2013, 2014  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.metamx.tranquility.druid

import com.fasterxml.jackson.databind.{Module, ObjectMapper}
import com.google.inject.{Binder, Key}
import io.druid.guice.JsonConfigProvider
import io.druid.guice.annotations.Self
import io.druid.initialization.{DruidModule, Initialization}
import io.druid.server.DruidNode
import java.{util => ju}
import scala.collection.JavaConverters._

object DruidGuicer
{
  private[this] val injector = {
    val startupInjector = Initialization.makeStartupInjector
    Initialization.makeInjectorWithModules(
      startupInjector,
      Seq[AnyRef](
        new DruidModule
        {
          def getJacksonModules = new ju.ArrayList[Module]()

          def configure(binder: Binder) {
            JsonConfigProvider.bindInstance(
              binder,
              Key.get(classOf[DruidNode], classOf[Self]),
              new DruidNode("dummy", "localhost", -1)
            )
          }
        }
      ).asJava
    )
  }

  def get[A: ClassManifest]: A = injector.getInstance(classManifest[A].erasure.asInstanceOf[Class[A]])

  def objectMapper = get[ObjectMapper]
}
