package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel.ProfileCompanionObject
import edu.ie3.util.TimeTools

/**
  * A power plant's profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
case class PowerPlantProfile(id: String,
                             profileType: PowerPlantProfileType,
                             profile: Map[ZonedDateTime, BigDecimal])
    extends ProfileModel[PowerPlantProfileType, BigDecimal]

case object PowerPlantProfile
    extends ProfileCompanionObject[PowerPlantProfile, BigDecimal] {
  val TIME = "time"
  val PP_111 = "pp_111"
  val PP_112 = "pp_112"
  val PP_113 = "pp_113"
  val PP_114 = "pp_114"
  val PP_115 = "pp_115"
  val PP_116 = "pp_116"
  val PP_117 = "pp_117"
  val PP_1 = "pp_1"
  val PP_2 = "pp_2"
  val PP_3 = "pp_3"
  val PP_4 = "pp_4"
  val PP_5 = "pp_5"
  val PP_6 = "pp_6"
  val PP_7 = "pp_7"
  val PP_8 = "pp_8"
  val PP_9 = "pp_9"
  val PP_10 = "pp_10"
  val PP_11 = "pp_11"
  val PP_12 = "pp_12"
  val PP_13 = "pp_13"
  val PP_14 = "pp_14"
  val PP_15 = "pp_15"
  val PP_16 = "pp_16"
  val PP_17 = "pp_17"
  val PP_18 = "pp_18"
  val PP_19 = "pp_19"
  val PP_20 = "pp_20"
  val PP_21 = "pp_21"
  val PP_22 = "pp_22"
  val PP_23 = "pp_23"
  val PP_24 = "pp_24"
  val PP_25 = "pp_25"
  val PP_26 = "pp_26"
  val PP_27 = "pp_27"
  val PP_28 = "pp_28"
  val PP_29 = "pp_29"
  val PP_30 = "pp_30"
  val PP_31 = "pp_31"
  val PP_32 = "pp_32"
  val PP_33 = "pp_33"
  val PP_34 = "pp_34"
  val PP_35 = "pp_35"
  val PP_36 = "pp_36"
  val PP_37 = "pp_37"
  val PP_38 = "pp_38"
  val PP_39 = "pp_39"
  val PP_40 = "pp_40"
  val PP_41 = "pp_41"
  val PP_42 = "pp_42"
  val PP_43 = "pp_43"
  val PP_44 = "pp_44"
  val PP_45 = "pp_45"
  val PP_46 = "pp_46"
  val PP_47 = "pp_47"
  val PP_48 = "pp_48"
  val PP_49 = "pp_49"
  val PP_50 = "pp_50"
  val PP_51 = "pp_51"
  val PP_52 = "pp_52"
  val PP_53 = "pp_53"
  val PP_54 = "pp_54"
  val PP_55 = "pp_55"
  val PP_56 = "pp_56"
  val PP_57 = "pp_57"
  val PP_58 = "pp_58"
  val PP_59 = "pp_59"
  val PP_60 = "pp_60"
  val PP_61 = "pp_61"
  val PP_62 = "pp_62"
  val PP_63 = "pp_63"
  val PP_64 = "pp_64"
  val PP_65 = "pp_65"
  val PP_66 = "pp_66"
  val PP_67 = "pp_67"
  val PP_68 = "pp_68"
  val PP_69 = "pp_69"
  val PP_70 = "pp_70"
  val PP_71 = "pp_71"
  val PP_72 = "pp_72"
  val PP_73 = "pp_73"
  val PP_74 = "pp_74"
  val PP_75 = "pp_75"
  val PP_76 = "pp_76"
  val PP_77 = "pp_77"
  val PP_78 = "pp_78"
  val PP_79 = "pp_79"
  val PP_80 = "pp_80"
  val PP_81 = "pp_81"
  val PP_82 = "pp_82"
  val PP_83 = "pp_83"
  val PP_84 = "pp_84"
  val PP_85 = "pp_85"
  val PP_86 = "pp_86"
  val PP_87 = "pp_87"
  val PP_88 = "pp_88"
  val PP_89 = "pp_89"
  val PP_90 = "pp_90"
  val PP_91 = "pp_91"
  val PP_92 = "pp_92"
  val PP_93 = "pp_93"
  val PP_94 = "pp_94"
  val PP_95 = "pp_95"
  val PP_96 = "pp_96"
  val PP_97 = "pp_97"
  val PP_98 = "pp_98"
  val PP_99 = "pp_99"
  val PP_100 = "pp_100"
  val PP_101 = "pp_101"
  val PP_102 = "pp_102"
  val PP_103 = "pp_103"
  val PP_104 = "pp_104"
  val PP_105 = "pp_105"
  val PP_106 = "pp_106"
  val PP_107 = "pp_107"
  val PP_108 = "pp_108"
  val PP_109 = "pp_109"
  val PP_110 = "pp_110"
  val PP_157 = "pp_157"
  val PP_158 = "pp_158"
  val PP_159 = "pp_159"
  val PP_160 = "pp_160"
  val PP_161 = "pp_161"
  val PP_162 = "pp_162"
  val PP_163 = "pp_163"
  val PP_164 = "pp_164"
  val PP_165 = "pp_165"
  val PP_166 = "pp_166"
  val PP_167 = "pp_167"
  val PP_168 = "pp_168"
  val PP_169 = "pp_169"
  val PP_170 = "pp_170"
  val PP_171 = "pp_171"
  val PP_172 = "pp_172"
  val PP_173 = "pp_173"
  val PP_174 = "pp_174"
  val PP_175 = "pp_175"
  val PP_176 = "pp_176"
  val PP_177 = "pp_177"
  val PP_178 = "pp_178"
  val PP_179 = "pp_179"
  val PP_180 = "pp_180"
  val PP_181 = "pp_181"
  val PP_182 = "pp_182"
  val PP_183 = "pp_183"
  val PP_184 = "pp_184"
  val PP_185 = "pp_185"
  val PP_186 = "pp_186"
  val PP_187 = "pp_187"
  val PP_188 = "pp_188"
  val PP_189 = "pp_189"
  val PP_190 = "pp_190"
  val PP_191 = "pp_191"
  val PP_192 = "pp_192"
  val PP_193 = "pp_193"
  val PP_194 = "pp_194"
  val PP_195 = "pp_195"
  val PP_196 = "pp_196"
  val PP_197 = "pp_197"
  val PP_198 = "pp_198"
  val PP_199 = "pp_199"
  val PP_200 = "pp_200"
  val PP_201 = "pp_201"
  val PP_202 = "pp_202"
  val PP_203 = "pp_203"
  val PP_204 = "pp_204"
  val PP_205 = "pp_205"
  val PP_206 = "pp_206"
  val PP_207 = "pp_207"
  val PP_208 = "pp_208"
  val PP_209 = "pp_209"
  val PP_210 = "pp_210"
  val PP_211 = "pp_211"
  val PP_212 = "pp_212"
  val PP_213 = "pp_213"
  val PP_214 = "pp_214"
  val PP_215 = "pp_215"
  val PP_216 = "pp_216"
  val PP_217 = "pp_217"
  val PP_218 = "pp_218"
  val PP_219 = "pp_219"
  val PP_220 = "pp_220"
  val PP_221 = "pp_221"
  val PP_222 = "pp_222"
  val PP_223 = "pp_223"
  val PP_224 = "pp_224"
  val PP_225 = "pp_225"
  val PP_226 = "pp_226"
  val PP_227 = "pp_227"
  val PP_228 = "pp_228"
  val PP_229 = "pp_229"
  val PP_230 = "pp_230"
  val PP_231 = "pp_231"
  val PP_232 = "pp_232"
  val PP_233 = "pp_233"
  val PP_234 = "pp_234"
  val PP_235 = "pp_235"
  val PP_236 = "pp_236"
  val PP_237 = "pp_237"
  val PP_238 = "pp_238"
  val PP_239 = "pp_239"
  val PP_240 = "pp_240"
  val PP_241 = "pp_241"
  val PP_242 = "pp_242"
  val PP_243 = "pp_243"
  val PP_244 = "pp_244"
  val PP_245 = "pp_245"
  val PP_246 = "pp_246"
  val PP_247 = "pp_247"
  val PP_248 = "pp_248"
  val PP_249 = "pp_249"
  val PP_250 = "pp_250"
  val PP_251 = "pp_251"
  val PP_252 = "pp_252"
  val PP_253 = "pp_253"
  val PP_254 = "pp_254"
  val PP_255 = "pp_255"
  val PP_256 = "pp_256"
  val PP_257 = "pp_257"
  val PP_258 = "pp_258"
  val PP_259 = "pp_259"
  val PP_260 = "pp_260"
  val PP_261 = "pp_261"
  val PP_262 = "pp_262"
  val PP_263 = "pp_263"
  val PP_264 = "pp_264"
  val PP_265 = "pp_265"
  val PP_266 = "pp_266"
  val PP_267 = "pp_267"
  val PP_268 = "pp_268"
  val PP_269 = "pp_269"
  val PP_270 = "pp_270"
  val PP_271 = "pp_271"
  val PP_272 = "pp_272"
  val PP_273 = "pp_273"
  val PP_274 = "pp_274"
  val PP_275 = "pp_275"
  val PP_276 = "pp_276"
  val PP_277 = "pp_277"
  val PP_278 = "pp_278"
  val PP_279 = "pp_279"
  val PP_280 = "pp_280"
  val PP_281 = "pp_281"
  val PP_282 = "pp_282"
  val PP_283 = "pp_283"
  val PP_284 = "pp_284"
  val PP_285 = "pp_285"
  val PP_286 = "pp_286"
  val PP_287 = "pp_287"
  val PP_288 = "pp_288"
  val PP_289 = "pp_289"
  val PP_290 = "pp_290"
  val PP_291 = "pp_291"
  val PP_292 = "pp_292"
  val PP_293 = "pp_293"
  val PP_294 = "pp_294"
  val PP_295 = "pp_295"
  val PP_296 = "pp_296"
  val PP_297 = "pp_297"
  val PP_298 = "pp_298"
  val PP_299 = "pp_299"
  val PP_300 = "pp_300"
  val PP_301 = "pp_301"
  val PP_302 = "pp_302"
  val PP_303 = "pp_303"
  val PP_304 = "pp_304"
  val PP_305 = "pp_305"
  val PP_306 = "pp_306"
  val PP_307 = "pp_307"
  val PP_308 = "pp_308"
  val PP_309 = "pp_309"
  val PP_310 = "pp_310"
  val PP_311 = "pp_311"
  val PP_312 = "pp_312"
  val PP_313 = "pp_313"
  val PP_314 = "pp_314"
  val PP_315 = "pp_315"
  val PP_316 = "pp_316"
  val PP_317 = "pp_317"
  val PP_318 = "pp_318"
  val PP_118 = "pp_118"
  val PP_119 = "pp_119"
  val PP_120 = "pp_120"
  val PP_121 = "pp_121"
  val PP_122 = "pp_122"
  val PP_123 = "pp_123"
  val PP_124 = "pp_124"
  val PP_125 = "pp_125"
  val PP_126 = "pp_126"
  val PP_127 = "pp_127"
  val PP_128 = "pp_128"
  val PP_129 = "pp_129"
  val PP_130 = "pp_130"
  val PP_131 = "pp_131"
  val PP_132 = "pp_132"
  val PP_133 = "pp_133"
  val PP_134 = "pp_134"
  val PP_135 = "pp_135"
  val PP_136 = "pp_136"
  val PP_137 = "pp_137"
  val PP_138 = "pp_138"
  val PP_139 = "pp_139"
  val PP_140 = "pp_140"
  val PP_141 = "pp_141"
  val PP_142 = "pp_142"
  val PP_143 = "pp_143"
  val PP_144 = "pp_144"
  val PP_145 = "pp_145"
  val PP_146 = "pp_146"
  val PP_147 = "pp_147"
  val PP_148 = "pp_148"
  val PP_149 = "pp_149"
  val PP_150 = "pp_150"
  val PP_151 = "pp_151"
  val PP_152 = "pp_152"
  val PP_153 = "pp_153"
  val PP_154 = "pp_154"
  val PP_155 = "pp_155"
  val PP_156 = "pp_156"
  val IMP0 = "imp0"
  val IMP1 = "imp1"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] = Array(
    MandatoryField(TIME),
    OptionalField(PP_111),
    OptionalField(PP_112),
    OptionalField(PP_113),
    OptionalField(PP_114),
    OptionalField(PP_115),
    OptionalField(PP_116),
    OptionalField(PP_117),
    OptionalField(PP_1),
    OptionalField(PP_2),
    OptionalField(PP_3),
    OptionalField(PP_4),
    OptionalField(PP_5),
    OptionalField(PP_6),
    OptionalField(PP_7),
    OptionalField(PP_8),
    OptionalField(PP_9),
    OptionalField(PP_10),
    OptionalField(PP_11),
    OptionalField(PP_12),
    OptionalField(PP_13),
    OptionalField(PP_14),
    OptionalField(PP_15),
    OptionalField(PP_16),
    OptionalField(PP_17),
    OptionalField(PP_18),
    OptionalField(PP_19),
    OptionalField(PP_20),
    OptionalField(PP_21),
    OptionalField(PP_22),
    OptionalField(PP_23),
    OptionalField(PP_24),
    OptionalField(PP_25),
    OptionalField(PP_26),
    OptionalField(PP_27),
    OptionalField(PP_28),
    OptionalField(PP_29),
    OptionalField(PP_30),
    OptionalField(PP_31),
    OptionalField(PP_32),
    OptionalField(PP_33),
    OptionalField(PP_34),
    OptionalField(PP_35),
    OptionalField(PP_36),
    OptionalField(PP_37),
    OptionalField(PP_38),
    OptionalField(PP_39),
    OptionalField(PP_40),
    OptionalField(PP_41),
    OptionalField(PP_42),
    OptionalField(PP_43),
    OptionalField(PP_44),
    OptionalField(PP_45),
    OptionalField(PP_46),
    OptionalField(PP_47),
    OptionalField(PP_48),
    OptionalField(PP_49),
    OptionalField(PP_50),
    OptionalField(PP_51),
    OptionalField(PP_52),
    OptionalField(PP_53),
    OptionalField(PP_54),
    OptionalField(PP_55),
    OptionalField(PP_56),
    OptionalField(PP_57),
    OptionalField(PP_58),
    OptionalField(PP_59),
    OptionalField(PP_60),
    OptionalField(PP_61),
    OptionalField(PP_62),
    OptionalField(PP_63),
    OptionalField(PP_64),
    OptionalField(PP_65),
    OptionalField(PP_66),
    OptionalField(PP_67),
    OptionalField(PP_68),
    OptionalField(PP_69),
    OptionalField(PP_70),
    OptionalField(PP_71),
    OptionalField(PP_72),
    OptionalField(PP_73),
    OptionalField(PP_74),
    OptionalField(PP_75),
    OptionalField(PP_76),
    OptionalField(PP_77),
    OptionalField(PP_78),
    OptionalField(PP_79),
    OptionalField(PP_80),
    OptionalField(PP_81),
    OptionalField(PP_82),
    OptionalField(PP_83),
    OptionalField(PP_84),
    OptionalField(PP_85),
    OptionalField(PP_86),
    OptionalField(PP_87),
    OptionalField(PP_88),
    OptionalField(PP_89),
    OptionalField(PP_90),
    OptionalField(PP_91),
    OptionalField(PP_92),
    OptionalField(PP_93),
    OptionalField(PP_94),
    OptionalField(PP_95),
    OptionalField(PP_96),
    OptionalField(PP_97),
    OptionalField(PP_98),
    OptionalField(PP_99),
    OptionalField(PP_100),
    OptionalField(PP_101),
    OptionalField(PP_102),
    OptionalField(PP_103),
    OptionalField(PP_104),
    OptionalField(PP_105),
    OptionalField(PP_106),
    OptionalField(PP_107),
    OptionalField(PP_108),
    OptionalField(PP_109),
    OptionalField(PP_110),
    OptionalField(PP_157),
    OptionalField(PP_158),
    OptionalField(PP_159),
    OptionalField(PP_160),
    OptionalField(PP_161),
    OptionalField(PP_162),
    OptionalField(PP_163),
    OptionalField(PP_164),
    OptionalField(PP_165),
    OptionalField(PP_166),
    OptionalField(PP_167),
    OptionalField(PP_168),
    OptionalField(PP_169),
    OptionalField(PP_170),
    OptionalField(PP_171),
    OptionalField(PP_172),
    OptionalField(PP_173),
    OptionalField(PP_174),
    OptionalField(PP_175),
    OptionalField(PP_176),
    OptionalField(PP_177),
    OptionalField(PP_178),
    OptionalField(PP_179),
    OptionalField(PP_180),
    OptionalField(PP_181),
    OptionalField(PP_182),
    OptionalField(PP_183),
    OptionalField(PP_184),
    OptionalField(PP_185),
    OptionalField(PP_186),
    OptionalField(PP_187),
    OptionalField(PP_188),
    OptionalField(PP_189),
    OptionalField(PP_190),
    OptionalField(PP_191),
    OptionalField(PP_192),
    OptionalField(PP_193),
    OptionalField(PP_194),
    OptionalField(PP_195),
    OptionalField(PP_196),
    OptionalField(PP_197),
    OptionalField(PP_198),
    OptionalField(PP_199),
    OptionalField(PP_200),
    OptionalField(PP_201),
    OptionalField(PP_202),
    OptionalField(PP_203),
    OptionalField(PP_204),
    OptionalField(PP_205),
    OptionalField(PP_206),
    OptionalField(PP_207),
    OptionalField(PP_208),
    OptionalField(PP_209),
    OptionalField(PP_210),
    OptionalField(PP_211),
    OptionalField(PP_212),
    OptionalField(PP_213),
    OptionalField(PP_214),
    OptionalField(PP_215),
    OptionalField(PP_216),
    OptionalField(PP_217),
    OptionalField(PP_218),
    OptionalField(PP_219),
    OptionalField(PP_220),
    OptionalField(PP_221),
    OptionalField(PP_222),
    OptionalField(PP_223),
    OptionalField(PP_224),
    OptionalField(PP_225),
    OptionalField(PP_226),
    OptionalField(PP_227),
    OptionalField(PP_228),
    OptionalField(PP_229),
    OptionalField(PP_230),
    OptionalField(PP_231),
    OptionalField(PP_232),
    OptionalField(PP_233),
    OptionalField(PP_234),
    OptionalField(PP_235),
    OptionalField(PP_236),
    OptionalField(PP_237),
    OptionalField(PP_238),
    OptionalField(PP_239),
    OptionalField(PP_240),
    OptionalField(PP_241),
    OptionalField(PP_242),
    OptionalField(PP_243),
    OptionalField(PP_244),
    OptionalField(PP_245),
    OptionalField(PP_246),
    OptionalField(PP_247),
    OptionalField(PP_248),
    OptionalField(PP_249),
    OptionalField(PP_250),
    OptionalField(PP_251),
    OptionalField(PP_252),
    OptionalField(PP_253),
    OptionalField(PP_254),
    OptionalField(PP_255),
    OptionalField(PP_256),
    OptionalField(PP_257),
    OptionalField(PP_258),
    OptionalField(PP_259),
    OptionalField(PP_260),
    OptionalField(PP_261),
    OptionalField(PP_262),
    OptionalField(PP_263),
    OptionalField(PP_264),
    OptionalField(PP_265),
    OptionalField(PP_266),
    OptionalField(PP_267),
    OptionalField(PP_268),
    OptionalField(PP_269),
    OptionalField(PP_270),
    OptionalField(PP_271),
    OptionalField(PP_272),
    OptionalField(PP_273),
    OptionalField(PP_274),
    OptionalField(PP_275),
    OptionalField(PP_276),
    OptionalField(PP_277),
    OptionalField(PP_278),
    OptionalField(PP_279),
    OptionalField(PP_280),
    OptionalField(PP_281),
    OptionalField(PP_282),
    OptionalField(PP_283),
    OptionalField(PP_284),
    OptionalField(PP_285),
    OptionalField(PP_286),
    OptionalField(PP_287),
    OptionalField(PP_288),
    OptionalField(PP_289),
    OptionalField(PP_290),
    OptionalField(PP_291),
    OptionalField(PP_292),
    OptionalField(PP_293),
    OptionalField(PP_294),
    OptionalField(PP_295),
    OptionalField(PP_296),
    OptionalField(PP_297),
    OptionalField(PP_298),
    OptionalField(PP_299),
    OptionalField(PP_300),
    OptionalField(PP_301),
    OptionalField(PP_302),
    OptionalField(PP_303),
    OptionalField(PP_304),
    OptionalField(PP_305),
    OptionalField(PP_306),
    OptionalField(PP_307),
    OptionalField(PP_308),
    OptionalField(PP_309),
    OptionalField(PP_310),
    OptionalField(PP_311),
    OptionalField(PP_312),
    OptionalField(PP_313),
    OptionalField(PP_314),
    OptionalField(PP_315),
    OptionalField(PP_316),
    OptionalField(PP_317),
    OptionalField(PP_318),
    OptionalField(PP_118),
    OptionalField(PP_119),
    OptionalField(PP_120),
    OptionalField(PP_121),
    OptionalField(PP_122),
    OptionalField(PP_123),
    OptionalField(PP_124),
    OptionalField(PP_125),
    OptionalField(PP_126),
    OptionalField(PP_127),
    OptionalField(PP_128),
    OptionalField(PP_129),
    OptionalField(PP_130),
    OptionalField(PP_131),
    OptionalField(PP_132),
    OptionalField(PP_133),
    OptionalField(PP_134),
    OptionalField(PP_135),
    OptionalField(PP_136),
    OptionalField(PP_137),
    OptionalField(PP_138),
    OptionalField(PP_139),
    OptionalField(PP_140),
    OptionalField(PP_141),
    OptionalField(PP_142),
    OptionalField(PP_143),
    OptionalField(PP_144),
    OptionalField(PP_145),
    OptionalField(PP_146),
    OptionalField(PP_147),
    OptionalField(PP_148),
    OptionalField(PP_149),
    OptionalField(PP_150),
    OptionalField(PP_151),
    OptionalField(PP_152),
    OptionalField(PP_153),
    OptionalField(PP_154),
    OptionalField(PP_155),
    OptionalField(PP_156),
    OptionalField(IMP0),
    OptionalField(IMP1)
  )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(
      rawData: Vector[RawModelData]): Vector[PowerPlantProfile] = {
    /* Determine the ids of the available load profiles by filtering the head line fields */
    val profileTypeStrings =
      super.determineAvailableProfileIds(rawData, None)

    /* Go through each line of the raw data table and extract the time stamp */
    (for (rawTableLine <- rawData) yield {
      val time = TimeTools.toZonedDateTime(rawTableLine.get(ProfileModel.TIME))

      /* Get the active and reactive power for each available load profile */
      for (typeString <- profileTypeStrings) yield {
        val profileType = PowerPlantProfileType(typeString)
        val factor = BigDecimal(rawTableLine.get(typeString))
        (profileType, time, factor)
      }
    }).flatten /* Flatten everything to have Vector((profileType, time, factor)) */
      .groupBy(collectionEntry => collectionEntry._1) /* Build a Map(profileType -> (profileType, time, factor)) */
      .map(profileEntry => {
        /* Extract the needed information to build a LoadProfile for each profile type */
        val profileType = profileEntry._1
        val profileValues =
          profileEntry._2.map(entry => entry._2 -> entry._3).toMap

        PowerPlantProfile(
          "\\$$".r.replaceAllIn(profileType.getClass.getSimpleName, ""),
          profileType,
          profileValues
        )
      })
      .toVector /* Finally build the Vector(PowerPlantProfile) */
  }
}
