package utils

import chisel3._

trait CoreConfig {
  val xlen = 64.W
}

abstract class ShaheCoreModule extends Module with CoreConfig

abstract class ShaheCoreBundle extends Bundle with CoreConfig