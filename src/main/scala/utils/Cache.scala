package utils
import utils.ShaheCoreModule

class CacheTemplate(set_num: Int = 128, ways_per_set: Int = 1, blocks_per_way: Int = 32, cache_type: Int = 0) extends ShaheCoreModule {
  /*
  Cache Template
    if cache_type = 0, build an instruction Cache. Otherwise, build a data Cache.
    Every block is 8 bit, or a byte.
    For a typical 4KiB instruction Cache, blocks_per_way is 32, ways_per_set is 1 (a directly-mapped Cache), set_num = 128.
    For a typical 8KiB L1 Data Cache, blocks_per_way is 32, ways_per_set is 4, set_num = 64.
   */


}

class CacheSet(ways_per_set: Int = 1, blocks_per_way: Int = 32) extends ShaheCoreModule {
  val readway = Log2Ce
}