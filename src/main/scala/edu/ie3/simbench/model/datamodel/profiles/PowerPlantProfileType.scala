package edu.ie3.simbench.model.datamodel.profiles

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Enumeration of available power plant profiles
  */
sealed trait PowerPlantProfileType extends ProfileType

object PowerPlantProfileType {
  case object PowerPlantProfile1 extends PowerPlantProfileType
  case object PowerPlantProfile10 extends PowerPlantProfileType
  case object PowerPlantProfile100 extends PowerPlantProfileType
  case object PowerPlantProfile101 extends PowerPlantProfileType
  case object PowerPlantProfile102 extends PowerPlantProfileType
  case object PowerPlantProfile103 extends PowerPlantProfileType
  case object PowerPlantProfile104 extends PowerPlantProfileType
  case object PowerPlantProfile105 extends PowerPlantProfileType
  case object PowerPlantProfile106 extends PowerPlantProfileType
  case object PowerPlantProfile107 extends PowerPlantProfileType
  case object PowerPlantProfile108 extends PowerPlantProfileType
  case object PowerPlantProfile109 extends PowerPlantProfileType
  case object PowerPlantProfile11 extends PowerPlantProfileType
  case object PowerPlantProfile110 extends PowerPlantProfileType
  case object PowerPlantProfile111 extends PowerPlantProfileType
  case object PowerPlantProfile112 extends PowerPlantProfileType
  case object PowerPlantProfile113 extends PowerPlantProfileType
  case object PowerPlantProfile114 extends PowerPlantProfileType
  case object PowerPlantProfile115 extends PowerPlantProfileType
  case object PowerPlantProfile116 extends PowerPlantProfileType
  case object PowerPlantProfile117 extends PowerPlantProfileType
  case object PowerPlantProfile118 extends PowerPlantProfileType
  case object PowerPlantProfile119 extends PowerPlantProfileType
  case object PowerPlantProfile12 extends PowerPlantProfileType
  case object PowerPlantProfile120 extends PowerPlantProfileType
  case object PowerPlantProfile121 extends PowerPlantProfileType
  case object PowerPlantProfile122 extends PowerPlantProfileType
  case object PowerPlantProfile123 extends PowerPlantProfileType
  case object PowerPlantProfile124 extends PowerPlantProfileType
  case object PowerPlantProfile125 extends PowerPlantProfileType
  case object PowerPlantProfile126 extends PowerPlantProfileType
  case object PowerPlantProfile127 extends PowerPlantProfileType
  case object PowerPlantProfile128 extends PowerPlantProfileType
  case object PowerPlantProfile129 extends PowerPlantProfileType
  case object PowerPlantProfile13 extends PowerPlantProfileType
  case object PowerPlantProfile130 extends PowerPlantProfileType
  case object PowerPlantProfile131 extends PowerPlantProfileType
  case object PowerPlantProfile132 extends PowerPlantProfileType
  case object PowerPlantProfile133 extends PowerPlantProfileType
  case object PowerPlantProfile134 extends PowerPlantProfileType
  case object PowerPlantProfile135 extends PowerPlantProfileType
  case object PowerPlantProfile136 extends PowerPlantProfileType
  case object PowerPlantProfile137 extends PowerPlantProfileType
  case object PowerPlantProfile138 extends PowerPlantProfileType
  case object PowerPlantProfile139 extends PowerPlantProfileType
  case object PowerPlantProfile14 extends PowerPlantProfileType
  case object PowerPlantProfile140 extends PowerPlantProfileType
  case object PowerPlantProfile141 extends PowerPlantProfileType
  case object PowerPlantProfile142 extends PowerPlantProfileType
  case object PowerPlantProfile143 extends PowerPlantProfileType
  case object PowerPlantProfile144 extends PowerPlantProfileType
  case object PowerPlantProfile145 extends PowerPlantProfileType
  case object PowerPlantProfile146 extends PowerPlantProfileType
  case object PowerPlantProfile147 extends PowerPlantProfileType
  case object PowerPlantProfile148 extends PowerPlantProfileType
  case object PowerPlantProfile149 extends PowerPlantProfileType
  case object PowerPlantProfile15 extends PowerPlantProfileType
  case object PowerPlantProfile150 extends PowerPlantProfileType
  case object PowerPlantProfile151 extends PowerPlantProfileType
  case object PowerPlantProfile152 extends PowerPlantProfileType
  case object PowerPlantProfile153 extends PowerPlantProfileType
  case object PowerPlantProfile154 extends PowerPlantProfileType
  case object PowerPlantProfile155 extends PowerPlantProfileType
  case object PowerPlantProfile156 extends PowerPlantProfileType
  case object PowerPlantProfile157 extends PowerPlantProfileType
  case object PowerPlantProfile158 extends PowerPlantProfileType
  case object PowerPlantProfile159 extends PowerPlantProfileType
  case object PowerPlantProfile16 extends PowerPlantProfileType
  case object PowerPlantProfile160 extends PowerPlantProfileType
  case object PowerPlantProfile161 extends PowerPlantProfileType
  case object PowerPlantProfile162 extends PowerPlantProfileType
  case object PowerPlantProfile163 extends PowerPlantProfileType
  case object PowerPlantProfile164 extends PowerPlantProfileType
  case object PowerPlantProfile165 extends PowerPlantProfileType
  case object PowerPlantProfile166 extends PowerPlantProfileType
  case object PowerPlantProfile167 extends PowerPlantProfileType
  case object PowerPlantProfile168 extends PowerPlantProfileType
  case object PowerPlantProfile169 extends PowerPlantProfileType
  case object PowerPlantProfile17 extends PowerPlantProfileType
  case object PowerPlantProfile170 extends PowerPlantProfileType
  case object PowerPlantProfile171 extends PowerPlantProfileType
  case object PowerPlantProfile172 extends PowerPlantProfileType
  case object PowerPlantProfile173 extends PowerPlantProfileType
  case object PowerPlantProfile174 extends PowerPlantProfileType
  case object PowerPlantProfile175 extends PowerPlantProfileType
  case object PowerPlantProfile176 extends PowerPlantProfileType
  case object PowerPlantProfile177 extends PowerPlantProfileType
  case object PowerPlantProfile178 extends PowerPlantProfileType
  case object PowerPlantProfile179 extends PowerPlantProfileType
  case object PowerPlantProfile18 extends PowerPlantProfileType
  case object PowerPlantProfile180 extends PowerPlantProfileType
  case object PowerPlantProfile181 extends PowerPlantProfileType
  case object PowerPlantProfile182 extends PowerPlantProfileType
  case object PowerPlantProfile183 extends PowerPlantProfileType
  case object PowerPlantProfile184 extends PowerPlantProfileType
  case object PowerPlantProfile185 extends PowerPlantProfileType
  case object PowerPlantProfile186 extends PowerPlantProfileType
  case object PowerPlantProfile187 extends PowerPlantProfileType
  case object PowerPlantProfile188 extends PowerPlantProfileType
  case object PowerPlantProfile189 extends PowerPlantProfileType
  case object PowerPlantProfile19 extends PowerPlantProfileType
  case object PowerPlantProfile190 extends PowerPlantProfileType
  case object PowerPlantProfile191 extends PowerPlantProfileType
  case object PowerPlantProfile192 extends PowerPlantProfileType
  case object PowerPlantProfile193 extends PowerPlantProfileType
  case object PowerPlantProfile194 extends PowerPlantProfileType
  case object PowerPlantProfile195 extends PowerPlantProfileType
  case object PowerPlantProfile196 extends PowerPlantProfileType
  case object PowerPlantProfile197 extends PowerPlantProfileType
  case object PowerPlantProfile198 extends PowerPlantProfileType
  case object PowerPlantProfile199 extends PowerPlantProfileType
  case object PowerPlantProfile2 extends PowerPlantProfileType
  case object PowerPlantProfile20 extends PowerPlantProfileType
  case object PowerPlantProfile200 extends PowerPlantProfileType
  case object PowerPlantProfile201 extends PowerPlantProfileType
  case object PowerPlantProfile202 extends PowerPlantProfileType
  case object PowerPlantProfile203 extends PowerPlantProfileType
  case object PowerPlantProfile204 extends PowerPlantProfileType
  case object PowerPlantProfile205 extends PowerPlantProfileType
  case object PowerPlantProfile206 extends PowerPlantProfileType
  case object PowerPlantProfile207 extends PowerPlantProfileType
  case object PowerPlantProfile208 extends PowerPlantProfileType
  case object PowerPlantProfile209 extends PowerPlantProfileType
  case object PowerPlantProfile21 extends PowerPlantProfileType
  case object PowerPlantProfile210 extends PowerPlantProfileType
  case object PowerPlantProfile211 extends PowerPlantProfileType
  case object PowerPlantProfile212 extends PowerPlantProfileType
  case object PowerPlantProfile213 extends PowerPlantProfileType
  case object PowerPlantProfile214 extends PowerPlantProfileType
  case object PowerPlantProfile215 extends PowerPlantProfileType
  case object PowerPlantProfile216 extends PowerPlantProfileType
  case object PowerPlantProfile217 extends PowerPlantProfileType
  case object PowerPlantProfile218 extends PowerPlantProfileType
  case object PowerPlantProfile219 extends PowerPlantProfileType
  case object PowerPlantProfile22 extends PowerPlantProfileType
  case object PowerPlantProfile220 extends PowerPlantProfileType
  case object PowerPlantProfile221 extends PowerPlantProfileType
  case object PowerPlantProfile222 extends PowerPlantProfileType
  case object PowerPlantProfile223 extends PowerPlantProfileType
  case object PowerPlantProfile224 extends PowerPlantProfileType
  case object PowerPlantProfile225 extends PowerPlantProfileType
  case object PowerPlantProfile226 extends PowerPlantProfileType
  case object PowerPlantProfile227 extends PowerPlantProfileType
  case object PowerPlantProfile228 extends PowerPlantProfileType
  case object PowerPlantProfile229 extends PowerPlantProfileType
  case object PowerPlantProfile23 extends PowerPlantProfileType
  case object PowerPlantProfile230 extends PowerPlantProfileType
  case object PowerPlantProfile231 extends PowerPlantProfileType
  case object PowerPlantProfile232 extends PowerPlantProfileType
  case object PowerPlantProfile233 extends PowerPlantProfileType
  case object PowerPlantProfile234 extends PowerPlantProfileType
  case object PowerPlantProfile235 extends PowerPlantProfileType
  case object PowerPlantProfile236 extends PowerPlantProfileType
  case object PowerPlantProfile237 extends PowerPlantProfileType
  case object PowerPlantProfile238 extends PowerPlantProfileType
  case object PowerPlantProfile239 extends PowerPlantProfileType
  case object PowerPlantProfile24 extends PowerPlantProfileType
  case object PowerPlantProfile240 extends PowerPlantProfileType
  case object PowerPlantProfile241 extends PowerPlantProfileType
  case object PowerPlantProfile242 extends PowerPlantProfileType
  case object PowerPlantProfile243 extends PowerPlantProfileType
  case object PowerPlantProfile244 extends PowerPlantProfileType
  case object PowerPlantProfile245 extends PowerPlantProfileType
  case object PowerPlantProfile246 extends PowerPlantProfileType
  case object PowerPlantProfile247 extends PowerPlantProfileType
  case object PowerPlantProfile248 extends PowerPlantProfileType
  case object PowerPlantProfile249 extends PowerPlantProfileType
  case object PowerPlantProfile25 extends PowerPlantProfileType
  case object PowerPlantProfile250 extends PowerPlantProfileType
  case object PowerPlantProfile251 extends PowerPlantProfileType
  case object PowerPlantProfile252 extends PowerPlantProfileType
  case object PowerPlantProfile253 extends PowerPlantProfileType
  case object PowerPlantProfile254 extends PowerPlantProfileType
  case object PowerPlantProfile255 extends PowerPlantProfileType
  case object PowerPlantProfile256 extends PowerPlantProfileType
  case object PowerPlantProfile257 extends PowerPlantProfileType
  case object PowerPlantProfile258 extends PowerPlantProfileType
  case object PowerPlantProfile259 extends PowerPlantProfileType
  case object PowerPlantProfile26 extends PowerPlantProfileType
  case object PowerPlantProfile260 extends PowerPlantProfileType
  case object PowerPlantProfile261 extends PowerPlantProfileType
  case object PowerPlantProfile262 extends PowerPlantProfileType
  case object PowerPlantProfile263 extends PowerPlantProfileType
  case object PowerPlantProfile264 extends PowerPlantProfileType
  case object PowerPlantProfile265 extends PowerPlantProfileType
  case object PowerPlantProfile266 extends PowerPlantProfileType
  case object PowerPlantProfile267 extends PowerPlantProfileType
  case object PowerPlantProfile268 extends PowerPlantProfileType
  case object PowerPlantProfile269 extends PowerPlantProfileType
  case object PowerPlantProfile27 extends PowerPlantProfileType
  case object PowerPlantProfile270 extends PowerPlantProfileType
  case object PowerPlantProfile271 extends PowerPlantProfileType
  case object PowerPlantProfile272 extends PowerPlantProfileType
  case object PowerPlantProfile273 extends PowerPlantProfileType
  case object PowerPlantProfile274 extends PowerPlantProfileType
  case object PowerPlantProfile275 extends PowerPlantProfileType
  case object PowerPlantProfile276 extends PowerPlantProfileType
  case object PowerPlantProfile277 extends PowerPlantProfileType
  case object PowerPlantProfile278 extends PowerPlantProfileType
  case object PowerPlantProfile279 extends PowerPlantProfileType
  case object PowerPlantProfile28 extends PowerPlantProfileType
  case object PowerPlantProfile280 extends PowerPlantProfileType
  case object PowerPlantProfile281 extends PowerPlantProfileType
  case object PowerPlantProfile282 extends PowerPlantProfileType
  case object PowerPlantProfile283 extends PowerPlantProfileType
  case object PowerPlantProfile284 extends PowerPlantProfileType
  case object PowerPlantProfile285 extends PowerPlantProfileType
  case object PowerPlantProfile286 extends PowerPlantProfileType
  case object PowerPlantProfile287 extends PowerPlantProfileType
  case object PowerPlantProfile288 extends PowerPlantProfileType
  case object PowerPlantProfile289 extends PowerPlantProfileType
  case object PowerPlantProfile29 extends PowerPlantProfileType
  case object PowerPlantProfile290 extends PowerPlantProfileType
  case object PowerPlantProfile291 extends PowerPlantProfileType
  case object PowerPlantProfile292 extends PowerPlantProfileType
  case object PowerPlantProfile293 extends PowerPlantProfileType
  case object PowerPlantProfile294 extends PowerPlantProfileType
  case object PowerPlantProfile295 extends PowerPlantProfileType
  case object PowerPlantProfile296 extends PowerPlantProfileType
  case object PowerPlantProfile297 extends PowerPlantProfileType
  case object PowerPlantProfile298 extends PowerPlantProfileType
  case object PowerPlantProfile299 extends PowerPlantProfileType
  case object PowerPlantProfile3 extends PowerPlantProfileType
  case object PowerPlantProfile30 extends PowerPlantProfileType
  case object PowerPlantProfile300 extends PowerPlantProfileType
  case object PowerPlantProfile301 extends PowerPlantProfileType
  case object PowerPlantProfile302 extends PowerPlantProfileType
  case object PowerPlantProfile303 extends PowerPlantProfileType
  case object PowerPlantProfile304 extends PowerPlantProfileType
  case object PowerPlantProfile305 extends PowerPlantProfileType
  case object PowerPlantProfile306 extends PowerPlantProfileType
  case object PowerPlantProfile307 extends PowerPlantProfileType
  case object PowerPlantProfile308 extends PowerPlantProfileType
  case object PowerPlantProfile309 extends PowerPlantProfileType
  case object PowerPlantProfile31 extends PowerPlantProfileType
  case object PowerPlantProfile310 extends PowerPlantProfileType
  case object PowerPlantProfile311 extends PowerPlantProfileType
  case object PowerPlantProfile312 extends PowerPlantProfileType
  case object PowerPlantProfile313 extends PowerPlantProfileType
  case object PowerPlantProfile314 extends PowerPlantProfileType
  case object PowerPlantProfile315 extends PowerPlantProfileType
  case object PowerPlantProfile316 extends PowerPlantProfileType
  case object PowerPlantProfile317 extends PowerPlantProfileType
  case object PowerPlantProfile318 extends PowerPlantProfileType
  case object PowerPlantProfile32 extends PowerPlantProfileType
  case object PowerPlantProfile33 extends PowerPlantProfileType
  case object PowerPlantProfile34 extends PowerPlantProfileType
  case object PowerPlantProfile35 extends PowerPlantProfileType
  case object PowerPlantProfile36 extends PowerPlantProfileType
  case object PowerPlantProfile37 extends PowerPlantProfileType
  case object PowerPlantProfile38 extends PowerPlantProfileType
  case object PowerPlantProfile39 extends PowerPlantProfileType
  case object PowerPlantProfile4 extends PowerPlantProfileType
  case object PowerPlantProfile40 extends PowerPlantProfileType
  case object PowerPlantProfile41 extends PowerPlantProfileType
  case object PowerPlantProfile42 extends PowerPlantProfileType
  case object PowerPlantProfile43 extends PowerPlantProfileType
  case object PowerPlantProfile44 extends PowerPlantProfileType
  case object PowerPlantProfile45 extends PowerPlantProfileType
  case object PowerPlantProfile46 extends PowerPlantProfileType
  case object PowerPlantProfile47 extends PowerPlantProfileType
  case object PowerPlantProfile48 extends PowerPlantProfileType
  case object PowerPlantProfile49 extends PowerPlantProfileType
  case object PowerPlantProfile5 extends PowerPlantProfileType
  case object PowerPlantProfile50 extends PowerPlantProfileType
  case object PowerPlantProfile51 extends PowerPlantProfileType
  case object PowerPlantProfile52 extends PowerPlantProfileType
  case object PowerPlantProfile53 extends PowerPlantProfileType
  case object PowerPlantProfile54 extends PowerPlantProfileType
  case object PowerPlantProfile55 extends PowerPlantProfileType
  case object PowerPlantProfile56 extends PowerPlantProfileType
  case object PowerPlantProfile57 extends PowerPlantProfileType
  case object PowerPlantProfile58 extends PowerPlantProfileType
  case object PowerPlantProfile59 extends PowerPlantProfileType
  case object PowerPlantProfile6 extends PowerPlantProfileType
  case object PowerPlantProfile60 extends PowerPlantProfileType
  case object PowerPlantProfile61 extends PowerPlantProfileType
  case object PowerPlantProfile62 extends PowerPlantProfileType
  case object PowerPlantProfile63 extends PowerPlantProfileType
  case object PowerPlantProfile64 extends PowerPlantProfileType
  case object PowerPlantProfile65 extends PowerPlantProfileType
  case object PowerPlantProfile66 extends PowerPlantProfileType
  case object PowerPlantProfile67 extends PowerPlantProfileType
  case object PowerPlantProfile68 extends PowerPlantProfileType
  case object PowerPlantProfile69 extends PowerPlantProfileType
  case object PowerPlantProfile7 extends PowerPlantProfileType
  case object PowerPlantProfile70 extends PowerPlantProfileType
  case object PowerPlantProfile71 extends PowerPlantProfileType
  case object PowerPlantProfile72 extends PowerPlantProfileType
  case object PowerPlantProfile73 extends PowerPlantProfileType
  case object PowerPlantProfile74 extends PowerPlantProfileType
  case object PowerPlantProfile75 extends PowerPlantProfileType
  case object PowerPlantProfile76 extends PowerPlantProfileType
  case object PowerPlantProfile77 extends PowerPlantProfileType
  case object PowerPlantProfile78 extends PowerPlantProfileType
  case object PowerPlantProfile79 extends PowerPlantProfileType
  case object PowerPlantProfile8 extends PowerPlantProfileType
  case object PowerPlantProfile80 extends PowerPlantProfileType
  case object PowerPlantProfile81 extends PowerPlantProfileType
  case object PowerPlantProfile82 extends PowerPlantProfileType
  case object PowerPlantProfile83 extends PowerPlantProfileType
  case object PowerPlantProfile84 extends PowerPlantProfileType
  case object PowerPlantProfile85 extends PowerPlantProfileType
  case object PowerPlantProfile86 extends PowerPlantProfileType
  case object PowerPlantProfile87 extends PowerPlantProfileType
  case object PowerPlantProfile88 extends PowerPlantProfileType
  case object PowerPlantProfile89 extends PowerPlantProfileType
  case object PowerPlantProfile9 extends PowerPlantProfileType
  case object PowerPlantProfile90 extends PowerPlantProfileType
  case object PowerPlantProfile91 extends PowerPlantProfileType
  case object PowerPlantProfile92 extends PowerPlantProfileType
  case object PowerPlantProfile93 extends PowerPlantProfileType
  case object PowerPlantProfile94 extends PowerPlantProfileType
  case object PowerPlantProfile95 extends PowerPlantProfileType
  case object PowerPlantProfile96 extends PowerPlantProfileType
  case object PowerPlantProfile97 extends PowerPlantProfileType
  case object PowerPlantProfile98 extends PowerPlantProfileType
  case object PowerPlantProfile99 extends PowerPlantProfileType
  case object PowerPlantProfileimp0 extends PowerPlantProfileType
  case object PowerPlantProfileimp1 extends PowerPlantProfileType

  /**
    * Hands back a suitable [[PowerPlantProfileType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[PowerPlantProfileType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): PowerPlantProfileType =
    typeString.toLowerCase
      .replaceAll("[_-]+|(pp)+", "") match {
      case "1"    => PowerPlantProfile1
      case "10"   => PowerPlantProfile10
      case "100"  => PowerPlantProfile100
      case "101"  => PowerPlantProfile101
      case "102"  => PowerPlantProfile102
      case "103"  => PowerPlantProfile103
      case "104"  => PowerPlantProfile104
      case "105"  => PowerPlantProfile105
      case "106"  => PowerPlantProfile106
      case "107"  => PowerPlantProfile107
      case "108"  => PowerPlantProfile108
      case "109"  => PowerPlantProfile109
      case "11"   => PowerPlantProfile11
      case "110"  => PowerPlantProfile110
      case "111"  => PowerPlantProfile111
      case "112"  => PowerPlantProfile112
      case "113"  => PowerPlantProfile113
      case "114"  => PowerPlantProfile114
      case "115"  => PowerPlantProfile115
      case "116"  => PowerPlantProfile116
      case "117"  => PowerPlantProfile117
      case "118"  => PowerPlantProfile118
      case "119"  => PowerPlantProfile119
      case "12"   => PowerPlantProfile12
      case "120"  => PowerPlantProfile120
      case "121"  => PowerPlantProfile121
      case "122"  => PowerPlantProfile122
      case "123"  => PowerPlantProfile123
      case "124"  => PowerPlantProfile124
      case "125"  => PowerPlantProfile125
      case "126"  => PowerPlantProfile126
      case "127"  => PowerPlantProfile127
      case "128"  => PowerPlantProfile128
      case "129"  => PowerPlantProfile129
      case "13"   => PowerPlantProfile13
      case "130"  => PowerPlantProfile130
      case "131"  => PowerPlantProfile131
      case "132"  => PowerPlantProfile132
      case "133"  => PowerPlantProfile133
      case "134"  => PowerPlantProfile134
      case "135"  => PowerPlantProfile135
      case "136"  => PowerPlantProfile136
      case "137"  => PowerPlantProfile137
      case "138"  => PowerPlantProfile138
      case "139"  => PowerPlantProfile139
      case "14"   => PowerPlantProfile14
      case "140"  => PowerPlantProfile140
      case "141"  => PowerPlantProfile141
      case "142"  => PowerPlantProfile142
      case "143"  => PowerPlantProfile143
      case "144"  => PowerPlantProfile144
      case "145"  => PowerPlantProfile145
      case "146"  => PowerPlantProfile146
      case "147"  => PowerPlantProfile147
      case "148"  => PowerPlantProfile148
      case "149"  => PowerPlantProfile149
      case "15"   => PowerPlantProfile15
      case "150"  => PowerPlantProfile150
      case "151"  => PowerPlantProfile151
      case "152"  => PowerPlantProfile152
      case "153"  => PowerPlantProfile153
      case "154"  => PowerPlantProfile154
      case "155"  => PowerPlantProfile155
      case "156"  => PowerPlantProfile156
      case "157"  => PowerPlantProfile157
      case "158"  => PowerPlantProfile158
      case "159"  => PowerPlantProfile159
      case "16"   => PowerPlantProfile16
      case "160"  => PowerPlantProfile160
      case "161"  => PowerPlantProfile161
      case "162"  => PowerPlantProfile162
      case "163"  => PowerPlantProfile163
      case "164"  => PowerPlantProfile164
      case "165"  => PowerPlantProfile165
      case "166"  => PowerPlantProfile166
      case "167"  => PowerPlantProfile167
      case "168"  => PowerPlantProfile168
      case "169"  => PowerPlantProfile169
      case "17"   => PowerPlantProfile17
      case "170"  => PowerPlantProfile170
      case "171"  => PowerPlantProfile171
      case "172"  => PowerPlantProfile172
      case "173"  => PowerPlantProfile173
      case "174"  => PowerPlantProfile174
      case "175"  => PowerPlantProfile175
      case "176"  => PowerPlantProfile176
      case "177"  => PowerPlantProfile177
      case "178"  => PowerPlantProfile178
      case "179"  => PowerPlantProfile179
      case "18"   => PowerPlantProfile18
      case "180"  => PowerPlantProfile180
      case "181"  => PowerPlantProfile181
      case "182"  => PowerPlantProfile182
      case "183"  => PowerPlantProfile183
      case "184"  => PowerPlantProfile184
      case "185"  => PowerPlantProfile185
      case "186"  => PowerPlantProfile186
      case "187"  => PowerPlantProfile187
      case "188"  => PowerPlantProfile188
      case "189"  => PowerPlantProfile189
      case "19"   => PowerPlantProfile19
      case "190"  => PowerPlantProfile190
      case "191"  => PowerPlantProfile191
      case "192"  => PowerPlantProfile192
      case "193"  => PowerPlantProfile193
      case "194"  => PowerPlantProfile194
      case "195"  => PowerPlantProfile195
      case "196"  => PowerPlantProfile196
      case "197"  => PowerPlantProfile197
      case "198"  => PowerPlantProfile198
      case "199"  => PowerPlantProfile199
      case "2"    => PowerPlantProfile2
      case "20"   => PowerPlantProfile20
      case "200"  => PowerPlantProfile200
      case "201"  => PowerPlantProfile201
      case "202"  => PowerPlantProfile202
      case "203"  => PowerPlantProfile203
      case "204"  => PowerPlantProfile204
      case "205"  => PowerPlantProfile205
      case "206"  => PowerPlantProfile206
      case "207"  => PowerPlantProfile207
      case "208"  => PowerPlantProfile208
      case "209"  => PowerPlantProfile209
      case "21"   => PowerPlantProfile21
      case "210"  => PowerPlantProfile210
      case "211"  => PowerPlantProfile211
      case "212"  => PowerPlantProfile212
      case "213"  => PowerPlantProfile213
      case "214"  => PowerPlantProfile214
      case "215"  => PowerPlantProfile215
      case "216"  => PowerPlantProfile216
      case "217"  => PowerPlantProfile217
      case "218"  => PowerPlantProfile218
      case "219"  => PowerPlantProfile219
      case "22"   => PowerPlantProfile22
      case "220"  => PowerPlantProfile220
      case "221"  => PowerPlantProfile221
      case "222"  => PowerPlantProfile222
      case "223"  => PowerPlantProfile223
      case "224"  => PowerPlantProfile224
      case "225"  => PowerPlantProfile225
      case "226"  => PowerPlantProfile226
      case "227"  => PowerPlantProfile227
      case "228"  => PowerPlantProfile228
      case "229"  => PowerPlantProfile229
      case "23"   => PowerPlantProfile23
      case "230"  => PowerPlantProfile230
      case "231"  => PowerPlantProfile231
      case "232"  => PowerPlantProfile232
      case "233"  => PowerPlantProfile233
      case "234"  => PowerPlantProfile234
      case "235"  => PowerPlantProfile235
      case "236"  => PowerPlantProfile236
      case "237"  => PowerPlantProfile237
      case "238"  => PowerPlantProfile238
      case "239"  => PowerPlantProfile239
      case "24"   => PowerPlantProfile24
      case "240"  => PowerPlantProfile240
      case "241"  => PowerPlantProfile241
      case "242"  => PowerPlantProfile242
      case "243"  => PowerPlantProfile243
      case "244"  => PowerPlantProfile244
      case "245"  => PowerPlantProfile245
      case "246"  => PowerPlantProfile246
      case "247"  => PowerPlantProfile247
      case "248"  => PowerPlantProfile248
      case "249"  => PowerPlantProfile249
      case "25"   => PowerPlantProfile25
      case "250"  => PowerPlantProfile250
      case "251"  => PowerPlantProfile251
      case "252"  => PowerPlantProfile252
      case "253"  => PowerPlantProfile253
      case "254"  => PowerPlantProfile254
      case "255"  => PowerPlantProfile255
      case "256"  => PowerPlantProfile256
      case "257"  => PowerPlantProfile257
      case "258"  => PowerPlantProfile258
      case "259"  => PowerPlantProfile259
      case "26"   => PowerPlantProfile26
      case "260"  => PowerPlantProfile260
      case "261"  => PowerPlantProfile261
      case "262"  => PowerPlantProfile262
      case "263"  => PowerPlantProfile263
      case "264"  => PowerPlantProfile264
      case "265"  => PowerPlantProfile265
      case "266"  => PowerPlantProfile266
      case "267"  => PowerPlantProfile267
      case "268"  => PowerPlantProfile268
      case "269"  => PowerPlantProfile269
      case "27"   => PowerPlantProfile27
      case "270"  => PowerPlantProfile270
      case "271"  => PowerPlantProfile271
      case "272"  => PowerPlantProfile272
      case "273"  => PowerPlantProfile273
      case "274"  => PowerPlantProfile274
      case "275"  => PowerPlantProfile275
      case "276"  => PowerPlantProfile276
      case "277"  => PowerPlantProfile277
      case "278"  => PowerPlantProfile278
      case "279"  => PowerPlantProfile279
      case "28"   => PowerPlantProfile28
      case "280"  => PowerPlantProfile280
      case "281"  => PowerPlantProfile281
      case "282"  => PowerPlantProfile282
      case "283"  => PowerPlantProfile283
      case "284"  => PowerPlantProfile284
      case "285"  => PowerPlantProfile285
      case "286"  => PowerPlantProfile286
      case "287"  => PowerPlantProfile287
      case "288"  => PowerPlantProfile288
      case "289"  => PowerPlantProfile289
      case "29"   => PowerPlantProfile29
      case "290"  => PowerPlantProfile290
      case "291"  => PowerPlantProfile291
      case "292"  => PowerPlantProfile292
      case "293"  => PowerPlantProfile293
      case "294"  => PowerPlantProfile294
      case "295"  => PowerPlantProfile295
      case "296"  => PowerPlantProfile296
      case "297"  => PowerPlantProfile297
      case "298"  => PowerPlantProfile298
      case "299"  => PowerPlantProfile299
      case "3"    => PowerPlantProfile3
      case "30"   => PowerPlantProfile30
      case "300"  => PowerPlantProfile300
      case "301"  => PowerPlantProfile301
      case "302"  => PowerPlantProfile302
      case "303"  => PowerPlantProfile303
      case "304"  => PowerPlantProfile304
      case "305"  => PowerPlantProfile305
      case "306"  => PowerPlantProfile306
      case "307"  => PowerPlantProfile307
      case "308"  => PowerPlantProfile308
      case "309"  => PowerPlantProfile309
      case "31"   => PowerPlantProfile31
      case "310"  => PowerPlantProfile310
      case "311"  => PowerPlantProfile311
      case "312"  => PowerPlantProfile312
      case "313"  => PowerPlantProfile313
      case "314"  => PowerPlantProfile314
      case "315"  => PowerPlantProfile315
      case "316"  => PowerPlantProfile316
      case "317"  => PowerPlantProfile317
      case "318"  => PowerPlantProfile318
      case "32"   => PowerPlantProfile32
      case "33"   => PowerPlantProfile33
      case "34"   => PowerPlantProfile34
      case "35"   => PowerPlantProfile35
      case "36"   => PowerPlantProfile36
      case "37"   => PowerPlantProfile37
      case "38"   => PowerPlantProfile38
      case "39"   => PowerPlantProfile39
      case "4"    => PowerPlantProfile4
      case "40"   => PowerPlantProfile40
      case "41"   => PowerPlantProfile41
      case "42"   => PowerPlantProfile42
      case "43"   => PowerPlantProfile43
      case "44"   => PowerPlantProfile44
      case "45"   => PowerPlantProfile45
      case "46"   => PowerPlantProfile46
      case "47"   => PowerPlantProfile47
      case "48"   => PowerPlantProfile48
      case "49"   => PowerPlantProfile49
      case "5"    => PowerPlantProfile5
      case "50"   => PowerPlantProfile50
      case "51"   => PowerPlantProfile51
      case "52"   => PowerPlantProfile52
      case "53"   => PowerPlantProfile53
      case "54"   => PowerPlantProfile54
      case "55"   => PowerPlantProfile55
      case "56"   => PowerPlantProfile56
      case "57"   => PowerPlantProfile57
      case "58"   => PowerPlantProfile58
      case "59"   => PowerPlantProfile59
      case "6"    => PowerPlantProfile6
      case "60"   => PowerPlantProfile60
      case "61"   => PowerPlantProfile61
      case "62"   => PowerPlantProfile62
      case "63"   => PowerPlantProfile63
      case "64"   => PowerPlantProfile64
      case "65"   => PowerPlantProfile65
      case "66"   => PowerPlantProfile66
      case "67"   => PowerPlantProfile67
      case "68"   => PowerPlantProfile68
      case "69"   => PowerPlantProfile69
      case "7"    => PowerPlantProfile7
      case "70"   => PowerPlantProfile70
      case "71"   => PowerPlantProfile71
      case "72"   => PowerPlantProfile72
      case "73"   => PowerPlantProfile73
      case "74"   => PowerPlantProfile74
      case "75"   => PowerPlantProfile75
      case "76"   => PowerPlantProfile76
      case "77"   => PowerPlantProfile77
      case "78"   => PowerPlantProfile78
      case "79"   => PowerPlantProfile79
      case "8"    => PowerPlantProfile8
      case "80"   => PowerPlantProfile80
      case "81"   => PowerPlantProfile81
      case "82"   => PowerPlantProfile82
      case "83"   => PowerPlantProfile83
      case "84"   => PowerPlantProfile84
      case "85"   => PowerPlantProfile85
      case "86"   => PowerPlantProfile86
      case "87"   => PowerPlantProfile87
      case "88"   => PowerPlantProfile88
      case "89"   => PowerPlantProfile89
      case "9"    => PowerPlantProfile9
      case "90"   => PowerPlantProfile90
      case "91"   => PowerPlantProfile91
      case "92"   => PowerPlantProfile92
      case "93"   => PowerPlantProfile93
      case "94"   => PowerPlantProfile94
      case "95"   => PowerPlantProfile95
      case "96"   => PowerPlantProfile96
      case "97"   => PowerPlantProfile97
      case "98"   => PowerPlantProfile98
      case "99"   => PowerPlantProfile99
      case "imp0" => PowerPlantProfileimp0
      case "imp1" => PowerPlantProfileimp1
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the measurement variable $whatever"
        )
    }
}
