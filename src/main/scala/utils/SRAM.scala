package utils
import chisel3._
import chisel3.util.{Decoupled, PriorityEncoder}

class SRAMTemplateArbiter(width: Int, depth: Int, numPorts: Int) extends Module {
  val io = IO(new Bundle {
    val readPorts = Vec(numPorts, Flipped(Decoupled(UInt(width.W))))
    val writePorts = Vec(numPorts, Flipped(Decoupled(UInt(width.W))))
  })

  val sram = SyncReadMem(depth, UInt(width.W))

  // Arbiter for read ports
  val readArbiter = Module(new RRArbiter(UInt(width.W), numPorts))
  readArbiter.io.in <> io.readPorts
  readArbiter.io.out.ready := true.B

  // Arbiter for write ports
  val writeArbiter = Module(new RRArbiter(UInt(width.W), numPorts))
  writeArbiter.io.in <> io.writePorts
  writeArbiter.io.out.ready := true.B

  // Read operation
  val readAddr = readArbiter.io.out.bits
  val readData = sram.read(readAddr)

  // Write operation
  val writeAddr = writeArbiter.io.out.bits
  val writeData = writeArbiter.io.out.bits
  val writeEnable = writeArbiter.io.out.valid
  when(writeEnable) {
    sram.write(writeAddr, writeData)
  }

  // Connect output signals
  io.readPorts.foreach(_.bits := readData)
  io.readPorts.foreach(_.valid := readArbiter.io.out.valid)
  io.writePorts.foreach(_.ready := writeArbiter.io.out.ready)
}

class RRArbiter[T <: Data](dataType: T, n: Int) extends Module {
  val io = IO(new Bundle {
    val in = Vec(n, Flipped(Decoupled(dataType)))
    val out = Decoupled(dataType)
  })

  val arbiter = Module(new RRArbiterCore(dataType, n))
  arbiter.io.in <> io.in
  io.out <> arbiter.io.out
}

class RRArbiterCore[T <: Data](dataType: T, n: Int) extends Module {
  val io = IO(new Bundle {
    val in = Vec(n, Flipped(Decoupled(dataType)))
    val out = Decoupled(dataType)
  })

  val grant = PriorityEncoder(io.in.map(_.valid))

  io.in.zipWithIndex.foreach { case (in, i) =>
    in.ready := io.out.ready && grant === i.U
  }

  io.out.valid := io.in(grant).valid
  io.out.bits := io.in(grant).bits
}
