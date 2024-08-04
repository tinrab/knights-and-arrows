package com.badlogic.gdx.utils.compression.lzma;

import com.badlogic.gdx.utils.compression.ICodeProgress;
import com.badlogic.gdx.utils.compression.lz.BinTree;
import com.badlogic.gdx.utils.compression.rangecoder.BitTreeEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Encoder {
   public static final int EMatchFinderTypeBT2 = 0;
   public static final int EMatchFinderTypeBT4 = 1;
   static final int kIfinityPrice = 268435455;
   static byte[] g_FastPos = new byte[2048];
   int _state = Base.StateInit();
   byte _previousByte;
   int[] _repDistances = new int[4];
   static final int kDefaultDictionaryLogSize = 22;
   static final int kNumFastBytesDefault = 32;
   public static final int kNumLenSpecSymbols = 16;
   static final int kNumOpts = 4096;
   Encoder.Optimal[] _optimum = new Encoder.Optimal[4096];
   BinTree _matchFinder = null;
   com.badlogic.gdx.utils.compression.rangecoder.Encoder _rangeEncoder = new com.badlogic.gdx.utils.compression.rangecoder.Encoder();
   short[] _isMatch = new short[192];
   short[] _isRep = new short[12];
   short[] _isRepG0 = new short[12];
   short[] _isRepG1 = new short[12];
   short[] _isRepG2 = new short[12];
   short[] _isRep0Long = new short[192];
   BitTreeEncoder[] _posSlotEncoder = new BitTreeEncoder[4];
   short[] _posEncoders = new short[114];
   BitTreeEncoder _posAlignEncoder = new BitTreeEncoder(4);
   Encoder.LenPriceTableEncoder _lenEncoder = new Encoder.LenPriceTableEncoder();
   Encoder.LenPriceTableEncoder _repMatchLenEncoder = new Encoder.LenPriceTableEncoder();
   Encoder.LiteralEncoder _literalEncoder = new Encoder.LiteralEncoder();
   int[] _matchDistances = new int[548];
   int _numFastBytes = 32;
   int _longestMatchLength;
   int _numDistancePairs;
   int _additionalOffset;
   int _optimumEndIndex;
   int _optimumCurrentIndex;
   boolean _longestMatchWasFound;
   int[] _posSlotPrices = new int[256];
   int[] _distancesPrices = new int[512];
   int[] _alignPrices = new int[16];
   int _alignPriceCount;
   int _distTableSize = 44;
   int _posStateBits = 2;
   int _posStateMask = 3;
   int _numLiteralPosStateBits = 0;
   int _numLiteralContextBits = 3;
   int _dictionarySize = 4194304;
   int _dictionarySizePrev = -1;
   int _numFastBytesPrev = -1;
   long nowPos64;
   boolean _finished;
   InputStream _inStream;
   int _matchFinderType = 1;
   boolean _writeEndMark = false;
   boolean _needReleaseMFStream = false;
   int[] reps = new int[4];
   int[] repLens = new int[4];
   int backRes;
   long[] processedInSize = new long[1];
   long[] processedOutSize = new long[1];
   boolean[] finished = new boolean[1];
   public static final int kPropSize = 5;
   byte[] properties = new byte[5];
   int[] tempPrices = new int[128];
   int _matchPriceCount;

   static {
      int kFastSlots = 22;
      int c = 2;
      g_FastPos[0] = 0;
      g_FastPos[1] = 1;

      for(int slotFast = 2; slotFast < kFastSlots; ++slotFast) {
         int k = 1 << (slotFast >> 1) - 1;

         for(int j = 0; j < k; ++c) {
            g_FastPos[c] = (byte)slotFast;
            ++j;
         }
      }

   }

   static int GetPosSlot(int pos) {
      if (pos < 2048) {
         return g_FastPos[pos];
      } else {
         return pos < 2097152 ? g_FastPos[pos >> 10] + 20 : g_FastPos[pos >> 20] + 40;
      }
   }

   static int GetPosSlot2(int pos) {
      if (pos < 131072) {
         return g_FastPos[pos >> 6] + 12;
      } else {
         return pos < 134217728 ? g_FastPos[pos >> 16] + 32 : g_FastPos[pos >> 26] + 52;
      }
   }

   void BaseInit() {
      this._state = Base.StateInit();
      this._previousByte = 0;

      for(int i = 0; i < 4; ++i) {
         this._repDistances[i] = 0;
      }

   }

   void Create() {
      if (this._matchFinder == null) {
         BinTree bt = new BinTree();
         int numHashBytes = 4;
         if (this._matchFinderType == 0) {
            numHashBytes = 2;
         }

         bt.SetType(numHashBytes);
         this._matchFinder = bt;
      }

      this._literalEncoder.Create(this._numLiteralPosStateBits, this._numLiteralContextBits);
      if (this._dictionarySize != this._dictionarySizePrev || this._numFastBytesPrev != this._numFastBytes) {
         this._matchFinder.Create(this._dictionarySize, 4096, this._numFastBytes, 274);
         this._dictionarySizePrev = this._dictionarySize;
         this._numFastBytesPrev = this._numFastBytes;
      }
   }

   public Encoder() {
      int i;
      for(i = 0; i < 4096; ++i) {
         this._optimum[i] = new Encoder.Optimal();
      }

      for(i = 0; i < 4; ++i) {
         this._posSlotEncoder[i] = new BitTreeEncoder(6);
      }

   }

   void SetWriteEndMarkerMode(boolean writeEndMarker) {
      this._writeEndMark = writeEndMarker;
   }

   void Init() {
      this.BaseInit();
      this._rangeEncoder.Init();
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isMatch);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep0Long);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG0);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG1);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG2);
      com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._posEncoders);
      this._literalEncoder.Init();

      for(int i = 0; i < 4; ++i) {
         this._posSlotEncoder[i].Init();
      }

      this._lenEncoder.Init(1 << this._posStateBits);
      this._repMatchLenEncoder.Init(1 << this._posStateBits);
      this._posAlignEncoder.Init();
      this._longestMatchWasFound = false;
      this._optimumEndIndex = 0;
      this._optimumCurrentIndex = 0;
      this._additionalOffset = 0;
   }

   int ReadMatchDistances() throws IOException {
      int lenRes = 0;
      this._numDistancePairs = this._matchFinder.GetMatches(this._matchDistances);
      if (this._numDistancePairs > 0) {
         lenRes = this._matchDistances[this._numDistancePairs - 2];
         if (lenRes == this._numFastBytes) {
            lenRes += this._matchFinder.GetMatchLen(lenRes - 1, this._matchDistances[this._numDistancePairs - 1], 273 - lenRes);
         }
      }

      ++this._additionalOffset;
      return lenRes;
   }

   void MovePos(int num) throws IOException {
      if (num > 0) {
         this._matchFinder.Skip(num);
         this._additionalOffset += num;
      }

   }

   int GetRepLen1Price(int state, int posState) {
      return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep0Long[(state << 4) + posState]);
   }

   int GetPureRepPrice(int repIndex, int state, int posState) {
      int price;
      if (repIndex == 0) {
         price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]);
         price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep0Long[(state << 4) + posState]);
      } else {
         price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG0[state]);
         if (repIndex == 1) {
            price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG1[state]);
         } else {
            price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG1[state]);
            price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this._isRepG2[state], repIndex - 2);
         }
      }

      return price;
   }

   int GetRepPrice(int repIndex, int len, int state, int posState) {
      int price = this._repMatchLenEncoder.GetPrice(len - 2, posState);
      return price + this.GetPureRepPrice(repIndex, state, posState);
   }

   int GetPosLenPrice(int pos, int len, int posState) {
      int lenToPosState = Base.GetLenToPosState(len);
      int price;
      if (pos < 128) {
         price = this._distancesPrices[lenToPosState * 128 + pos];
      } else {
         price = this._posSlotPrices[(lenToPosState << 6) + GetPosSlot2(pos)] + this._alignPrices[pos & 15];
      }

      return price + this._lenEncoder.GetPrice(len - 2, posState);
   }

   int Backward(int cur) {
      this._optimumEndIndex = cur;
      int posMem = this._optimum[cur].PosPrev;
      int backMem = this._optimum[cur].BackPrev;

      int posPrev;
      do {
         if (this._optimum[cur].Prev1IsChar) {
            this._optimum[posMem].MakeAsChar();
            this._optimum[posMem].PosPrev = posMem - 1;
            if (this._optimum[cur].Prev2) {
               this._optimum[posMem - 1].Prev1IsChar = false;
               this._optimum[posMem - 1].PosPrev = this._optimum[cur].PosPrev2;
               this._optimum[posMem - 1].BackPrev = this._optimum[cur].BackPrev2;
            }
         }

         posPrev = posMem;
         int backCur = backMem;
         backMem = this._optimum[posMem].BackPrev;
         posMem = this._optimum[posMem].PosPrev;
         this._optimum[posPrev].BackPrev = backCur;
         this._optimum[posPrev].PosPrev = cur;
         cur = posPrev;
      } while(posPrev > 0);

      this.backRes = this._optimum[0].BackPrev;
      this._optimumCurrentIndex = this._optimum[0].PosPrev;
      return this._optimumCurrentIndex;
   }

   int GetOptimum(int position) throws IOException {
      int lenMain;
      if (this._optimumEndIndex != this._optimumCurrentIndex) {
         lenMain = this._optimum[this._optimumCurrentIndex].PosPrev - this._optimumCurrentIndex;
         this.backRes = this._optimum[this._optimumCurrentIndex].BackPrev;
         this._optimumCurrentIndex = this._optimum[this._optimumCurrentIndex].PosPrev;
         return lenMain;
      } else {
         this._optimumCurrentIndex = this._optimumEndIndex = 0;
         if (!this._longestMatchWasFound) {
            lenMain = this.ReadMatchDistances();
         } else {
            lenMain = this._longestMatchLength;
            this._longestMatchWasFound = false;
         }

         int numDistancePairs = this._numDistancePairs;
         int numAvailableBytes = this._matchFinder.GetNumAvailableBytes() + 1;
         if (numAvailableBytes < 2) {
            this.backRes = -1;
            return 1;
         } else {
            if (numAvailableBytes > 273) {
               boolean var38 = true;
            }

            int repMaxIndex = 0;

            int i;
            for(i = 0; i < 4; ++i) {
               this.reps[i] = this._repDistances[i];
               this.repLens[i] = this._matchFinder.GetMatchLen(-1, this.reps[i], 273);
               if (this.repLens[i] > this.repLens[repMaxIndex]) {
                  repMaxIndex = i;
               }
            }

            if (this.repLens[repMaxIndex] >= this._numFastBytes) {
               this.backRes = repMaxIndex;
               int lenRes = this.repLens[repMaxIndex];
               this.MovePos(lenRes - 1);
               return lenRes;
            } else if (lenMain >= this._numFastBytes) {
               this.backRes = this._matchDistances[numDistancePairs - 1] + 4;
               this.MovePos(lenMain - 1);
               return lenMain;
            } else {
               byte currentByte = this._matchFinder.GetIndexByte(-1);
               byte matchByte = this._matchFinder.GetIndexByte(0 - this._repDistances[0] - 1 - 1);
               if (lenMain < 2 && currentByte != matchByte && this.repLens[repMaxIndex] < 2) {
                  this.backRes = -1;
                  return 1;
               } else {
                  this._optimum[0].State = this._state;
                  int posState = position & this._posStateMask;
                  this._optimum[1].Price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(this._state << 4) + posState]) + this._literalEncoder.GetSubCoder(position, this._previousByte).GetPrice(!Base.StateIsCharState(this._state), matchByte, currentByte);
                  this._optimum[1].MakeAsChar();
                  int matchPrice = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(this._state << 4) + posState]);
                  int repMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[this._state]);
                  int lenEnd;
                  if (matchByte == currentByte) {
                     lenEnd = repMatchPrice + this.GetRepLen1Price(this._state, posState);
                     if (lenEnd < this._optimum[1].Price) {
                        this._optimum[1].Price = lenEnd;
                        this._optimum[1].MakeAsShortRep();
                     }
                  }

                  lenEnd = lenMain >= this.repLens[repMaxIndex] ? lenMain : this.repLens[repMaxIndex];
                  if (lenEnd < 2) {
                     this.backRes = this._optimum[1].BackPrev;
                     return 1;
                  } else {
                     this._optimum[1].PosPrev = 0;
                     this._optimum[0].Backs0 = this.reps[0];
                     this._optimum[0].Backs1 = this.reps[1];
                     this._optimum[0].Backs2 = this.reps[2];
                     this._optimum[0].Backs3 = this.reps[3];
                     int len = lenEnd;

                     do {
                        this._optimum[len--].Price = 268435455;
                     } while(len >= 2);

                     int normalMatchPrice;
                     int cur;
                     int newLen;
                     for(i = 0; i < 4; ++i) {
                        normalMatchPrice = this.repLens[i];
                        if (normalMatchPrice >= 2) {
                           cur = repMatchPrice + this.GetPureRepPrice(i, this._state, posState);

                           do {
                              newLen = cur + this._repMatchLenEncoder.GetPrice(normalMatchPrice - 2, posState);
                              Encoder.Optimal optimum = this._optimum[normalMatchPrice];
                              if (newLen < optimum.Price) {
                                 optimum.Price = newLen;
                                 optimum.PosPrev = 0;
                                 optimum.BackPrev = i;
                                 optimum.Prev1IsChar = false;
                              }

                              --normalMatchPrice;
                           } while(normalMatchPrice >= 2);
                        }
                     }

                     normalMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[this._state]);
                     len = this.repLens[0] >= 2 ? this.repLens[0] + 1 : 2;
                     int posPrev;
                     if (len <= lenMain) {
                        for(cur = 0; len > this._matchDistances[cur]; cur += 2) {
                        }

                        while(true) {
                           newLen = this._matchDistances[cur + 1];
                           posPrev = normalMatchPrice + this.GetPosLenPrice(newLen, len, posState);
                           Encoder.Optimal optimum = this._optimum[len];
                           if (posPrev < optimum.Price) {
                              optimum.Price = posPrev;
                              optimum.PosPrev = 0;
                              optimum.BackPrev = newLen + 4;
                              optimum.Prev1IsChar = false;
                           }

                           if (len == this._matchDistances[cur]) {
                              cur += 2;
                              if (cur == numDistancePairs) {
                                 break;
                              }
                           }

                           ++len;
                        }
                     }

                     cur = 0;

                     while(true) {
                        ++cur;
                        if (cur == lenEnd) {
                           return this.Backward(cur);
                        }

                        newLen = this.ReadMatchDistances();
                        numDistancePairs = this._numDistancePairs;
                        if (newLen >= this._numFastBytes) {
                           this._longestMatchLength = newLen;
                           this._longestMatchWasFound = true;
                           return this.Backward(cur);
                        }

                        ++position;
                        posPrev = this._optimum[cur].PosPrev;
                        int state;
                        if (this._optimum[cur].Prev1IsChar) {
                           --posPrev;
                           if (this._optimum[cur].Prev2) {
                              state = this._optimum[this._optimum[cur].PosPrev2].State;
                              if (this._optimum[cur].BackPrev2 < 4) {
                                 state = Base.StateUpdateRep(state);
                              } else {
                                 state = Base.StateUpdateMatch(state);
                              }
                           } else {
                              state = this._optimum[posPrev].State;
                           }

                           state = Base.StateUpdateChar(state);
                        } else {
                           state = this._optimum[posPrev].State;
                        }

                        int pos;
                        if (posPrev == cur - 1) {
                           if (this._optimum[cur].IsShortRep()) {
                              state = Base.StateUpdateShortRep(state);
                           } else {
                              state = Base.StateUpdateChar(state);
                           }
                        } else {
                           if (this._optimum[cur].Prev1IsChar && this._optimum[cur].Prev2) {
                              posPrev = this._optimum[cur].PosPrev2;
                              pos = this._optimum[cur].BackPrev2;
                              state = Base.StateUpdateRep(state);
                           } else {
                              pos = this._optimum[cur].BackPrev;
                              if (pos < 4) {
                                 state = Base.StateUpdateRep(state);
                              } else {
                                 state = Base.StateUpdateMatch(state);
                              }
                           }

                           Encoder.Optimal opt = this._optimum[posPrev];
                           if (pos < 4) {
                              if (pos == 0) {
                                 this.reps[0] = opt.Backs0;
                                 this.reps[1] = opt.Backs1;
                                 this.reps[2] = opt.Backs2;
                                 this.reps[3] = opt.Backs3;
                              } else if (pos == 1) {
                                 this.reps[0] = opt.Backs1;
                                 this.reps[1] = opt.Backs0;
                                 this.reps[2] = opt.Backs2;
                                 this.reps[3] = opt.Backs3;
                              } else if (pos == 2) {
                                 this.reps[0] = opt.Backs2;
                                 this.reps[1] = opt.Backs0;
                                 this.reps[2] = opt.Backs1;
                                 this.reps[3] = opt.Backs3;
                              } else {
                                 this.reps[0] = opt.Backs3;
                                 this.reps[1] = opt.Backs0;
                                 this.reps[2] = opt.Backs1;
                                 this.reps[3] = opt.Backs2;
                              }
                           } else {
                              this.reps[0] = pos - 4;
                              this.reps[1] = opt.Backs0;
                              this.reps[2] = opt.Backs1;
                              this.reps[3] = opt.Backs2;
                           }
                        }

                        this._optimum[cur].State = state;
                        this._optimum[cur].Backs0 = this.reps[0];
                        this._optimum[cur].Backs1 = this.reps[1];
                        this._optimum[cur].Backs2 = this.reps[2];
                        this._optimum[cur].Backs3 = this.reps[3];
                        pos = this._optimum[cur].Price;
                        currentByte = this._matchFinder.GetIndexByte(-1);
                        matchByte = this._matchFinder.GetIndexByte(0 - this.reps[0] - 1 - 1);
                        posState = position & this._posStateMask;
                        int curAnd1Price = pos + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state << 4) + posState]) + this._literalEncoder.GetSubCoder(position, this._matchFinder.GetIndexByte(-2)).GetPrice(!Base.StateIsCharState(state), matchByte, currentByte);
                        Encoder.Optimal nextOptimum = this._optimum[cur + 1];
                        boolean nextIsChar = false;
                        if (curAnd1Price < nextOptimum.Price) {
                           nextOptimum.Price = curAnd1Price;
                           nextOptimum.PosPrev = cur;
                           nextOptimum.MakeAsChar();
                           nextIsChar = true;
                        }

                        matchPrice = pos + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state << 4) + posState]);
                        repMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state]);
                        int numAvailableBytesFull;
                        if (matchByte == currentByte && (nextOptimum.PosPrev >= cur || nextOptimum.BackPrev != 0)) {
                           numAvailableBytesFull = repMatchPrice + this.GetRepLen1Price(state, posState);
                           if (numAvailableBytesFull <= nextOptimum.Price) {
                              nextOptimum.Price = numAvailableBytesFull;
                              nextOptimum.PosPrev = cur;
                              nextOptimum.MakeAsShortRep();
                              nextIsChar = true;
                           }
                        }

                        numAvailableBytesFull = this._matchFinder.GetNumAvailableBytes() + 1;
                        numAvailableBytesFull = Math.min(4095 - cur, numAvailableBytesFull);
                        numAvailableBytes = numAvailableBytesFull;
                        if (numAvailableBytesFull >= 2) {
                           if (numAvailableBytesFull > this._numFastBytes) {
                              numAvailableBytes = this._numFastBytes;
                           }

                           int startLen;
                           int repIndex;
                           int lenTest;
                           int curBack;
                           int curAndLenPrice;
                           int lenTest2;
                           int state2;
                           if (!nextIsChar && matchByte != currentByte) {
                              startLen = Math.min(numAvailableBytesFull - 1, this._numFastBytes);
                              repIndex = this._matchFinder.GetMatchLen(0, this.reps[0], startLen);
                              if (repIndex >= 2) {
                                 lenTest = Base.StateUpdateChar(state);
                                 curBack = position + 1 & this._posStateMask;
                                 curAndLenPrice = curAnd1Price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(lenTest << 4) + curBack]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[lenTest]);

                                 for(lenTest2 = cur + 1 + repIndex; lenEnd < lenTest2; this._optimum[lenEnd].Price = 268435455) {
                                    ++lenEnd;
                                 }

                                 state2 = curAndLenPrice + this.GetRepPrice(0, repIndex, lenTest, curBack);
                                 Encoder.Optimal optimum = this._optimum[lenTest2];
                                 if (state2 < optimum.Price) {
                                    optimum.Price = state2;
                                    optimum.PosPrev = cur + 1;
                                    optimum.BackPrev = 0;
                                    optimum.Prev1IsChar = true;
                                    optimum.Prev2 = false;
                                 }
                              }
                           }

                           startLen = 2;

                           int state2;
                           int posStateNext;
                           int curAndLenCharPrice;
                           int nextMatchPrice;
                           int nextRepMatchPrice;
                           Encoder.Optimal optimum;
                           int lenTest2;
                           for(repIndex = 0; repIndex < 4; ++repIndex) {
                              lenTest = this._matchFinder.GetMatchLen(-1, this.reps[repIndex], numAvailableBytes);
                              if (lenTest >= 2) {
                                 curBack = lenTest;

                                 do {
                                    while(lenEnd < cur + lenTest) {
                                       ++lenEnd;
                                       this._optimum[lenEnd].Price = 268435455;
                                    }

                                    curAndLenPrice = repMatchPrice + this.GetRepPrice(repIndex, lenTest, state, posState);
                                    optimum = this._optimum[cur + lenTest];
                                    if (curAndLenPrice < optimum.Price) {
                                       optimum.Price = curAndLenPrice;
                                       optimum.PosPrev = cur;
                                       optimum.BackPrev = repIndex;
                                       optimum.Prev1IsChar = false;
                                    }

                                    --lenTest;
                                 } while(lenTest >= 2);

                                 if (repIndex == 0) {
                                    startLen = curBack + 1;
                                 }

                                 if (curBack < numAvailableBytesFull) {
                                    curAndLenPrice = Math.min(numAvailableBytesFull - 1 - curBack, this._numFastBytes);
                                    lenTest2 = this._matchFinder.GetMatchLen(curBack, this.reps[repIndex], curAndLenPrice);
                                    if (lenTest2 >= 2) {
                                       state2 = Base.StateUpdateRep(state);
                                       lenTest2 = position + curBack & this._posStateMask;
                                       state2 = repMatchPrice + this.GetRepPrice(repIndex, curBack, state, posState) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state2 << 4) + lenTest2]) + this._literalEncoder.GetSubCoder(position + curBack, this._matchFinder.GetIndexByte(curBack - 1 - 1)).GetPrice(true, this._matchFinder.GetIndexByte(curBack - 1 - (this.reps[repIndex] + 1)), this._matchFinder.GetIndexByte(curBack - 1));
                                       state2 = Base.StateUpdateChar(state2);
                                       lenTest2 = position + curBack + 1 & this._posStateMask;
                                       posStateNext = state2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + lenTest2]);
                                       curAndLenCharPrice = posStateNext + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state2]);

                                       for(nextMatchPrice = curBack + 1 + lenTest2; lenEnd < cur + nextMatchPrice; this._optimum[lenEnd].Price = 268435455) {
                                          ++lenEnd;
                                       }

                                       nextRepMatchPrice = curAndLenCharPrice + this.GetRepPrice(0, lenTest2, state2, lenTest2);
                                       Encoder.Optimal optimum = this._optimum[cur + nextMatchPrice];
                                       if (nextRepMatchPrice < optimum.Price) {
                                          optimum.Price = nextRepMatchPrice;
                                          optimum.PosPrev = cur + curBack + 1;
                                          optimum.BackPrev = 0;
                                          optimum.Prev1IsChar = true;
                                          optimum.Prev2 = true;
                                          optimum.PosPrev2 = cur;
                                          optimum.BackPrev2 = repIndex;
                                       }
                                    }
                                 }
                              }
                           }

                           if (newLen > numAvailableBytes) {
                              newLen = numAvailableBytes;

                              for(numDistancePairs = 0; newLen > this._matchDistances[numDistancePairs]; numDistancePairs += 2) {
                              }

                              this._matchDistances[numDistancePairs] = newLen;
                              numDistancePairs += 2;
                           }

                           if (newLen >= startLen) {
                              for(normalMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[state]); lenEnd < cur + newLen; this._optimum[lenEnd].Price = 268435455) {
                                 ++lenEnd;
                              }

                              for(repIndex = 0; startLen > this._matchDistances[repIndex]; repIndex += 2) {
                              }

                              lenTest = startLen;

                              while(true) {
                                 curBack = this._matchDistances[repIndex + 1];
                                 curAndLenPrice = normalMatchPrice + this.GetPosLenPrice(curBack, lenTest, posState);
                                 optimum = this._optimum[cur + lenTest];
                                 if (curAndLenPrice < optimum.Price) {
                                    optimum.Price = curAndLenPrice;
                                    optimum.PosPrev = cur;
                                    optimum.BackPrev = curBack + 4;
                                    optimum.Prev1IsChar = false;
                                 }

                                 if (lenTest == this._matchDistances[repIndex]) {
                                    if (lenTest < numAvailableBytesFull) {
                                       state2 = Math.min(numAvailableBytesFull - 1 - lenTest, this._numFastBytes);
                                       lenTest2 = this._matchFinder.GetMatchLen(lenTest, curBack, state2);
                                       if (lenTest2 >= 2) {
                                          state2 = Base.StateUpdateMatch(state);
                                          posStateNext = position + lenTest & this._posStateMask;
                                          curAndLenCharPrice = curAndLenPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state2 << 4) + posStateNext]) + this._literalEncoder.GetSubCoder(position + lenTest, this._matchFinder.GetIndexByte(lenTest - 1 - 1)).GetPrice(true, this._matchFinder.GetIndexByte(lenTest - (curBack + 1) - 1), this._matchFinder.GetIndexByte(lenTest - 1));
                                          state2 = Base.StateUpdateChar(state2);
                                          posStateNext = position + lenTest + 1 & this._posStateMask;
                                          nextMatchPrice = curAndLenCharPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posStateNext]);
                                          nextRepMatchPrice = nextMatchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state2]);

                                          int offset;
                                          for(offset = lenTest + 1 + lenTest2; lenEnd < cur + offset; this._optimum[lenEnd].Price = 268435455) {
                                             ++lenEnd;
                                          }

                                          curAndLenPrice = nextRepMatchPrice + this.GetRepPrice(0, lenTest2, state2, posStateNext);
                                          optimum = this._optimum[cur + offset];
                                          if (curAndLenPrice < optimum.Price) {
                                             optimum.Price = curAndLenPrice;
                                             optimum.PosPrev = cur + lenTest + 1;
                                             optimum.BackPrev = 0;
                                             optimum.Prev1IsChar = true;
                                             optimum.Prev2 = true;
                                             optimum.PosPrev2 = cur;
                                             optimum.BackPrev2 = curBack + 4;
                                          }
                                       }
                                    }

                                    repIndex += 2;
                                    if (repIndex == numDistancePairs) {
                                       break;
                                    }
                                 }

                                 ++lenTest;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   boolean ChangePair(int smallDist, int bigDist) {
      int kDif = 7;
      return smallDist < 1 << 32 - kDif && bigDist >= smallDist << kDif;
   }

   void WriteEndMarker(int posState) throws IOException {
      if (this._writeEndMark) {
         this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, 1);
         this._rangeEncoder.Encode(this._isRep, this._state, 0);
         this._state = Base.StateUpdateMatch(this._state);
         int len = 2;
         this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
         int posSlot = 63;
         int lenToPosState = Base.GetLenToPosState(len);
         this._posSlotEncoder[lenToPosState].Encode(this._rangeEncoder, posSlot);
         int footerBits = 30;
         int posReduced = (1 << footerBits) - 1;
         this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
         this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
      }
   }

   void Flush(int nowPos) throws IOException {
      this.ReleaseMFStream();
      this.WriteEndMarker(nowPos & this._posStateMask);
      this._rangeEncoder.FlushData();
      this._rangeEncoder.FlushStream();
   }

   public void CodeOneBlock(long[] inSize, long[] outSize, boolean[] finished) throws IOException {
      inSize[0] = 0L;
      outSize[0] = 0L;
      finished[0] = true;
      if (this._inStream != null) {
         this._matchFinder.SetStream(this._inStream);
         this._matchFinder.Init();
         this._needReleaseMFStream = true;
         this._inStream = null;
      }

      if (!this._finished) {
         this._finished = true;
         long progressPosValuePrev = this.nowPos64;
         int len;
         if (this.nowPos64 == 0L) {
            if (this._matchFinder.GetNumAvailableBytes() == 0) {
               this.Flush((int)this.nowPos64);
               return;
            }

            this.ReadMatchDistances();
            len = (int)this.nowPos64 & this._posStateMask;
            this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + len, 0);
            this._state = Base.StateUpdateChar(this._state);
            byte curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
            this._literalEncoder.GetSubCoder((int)this.nowPos64, this._previousByte).Encode(this._rangeEncoder, curByte);
            this._previousByte = curByte;
            --this._additionalOffset;
            ++this.nowPos64;
         }

         if (this._matchFinder.GetNumAvailableBytes() == 0) {
            this.Flush((int)this.nowPos64);
         } else {
            while(true) {
               len = this.GetOptimum((int)this.nowPos64);
               int pos = this.backRes;
               int posState = (int)this.nowPos64 & this._posStateMask;
               int complexState = (this._state << 4) + posState;
               if (len == 1 && pos == -1) {
                  this._rangeEncoder.Encode(this._isMatch, complexState, 0);
                  byte curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                  Encoder.LiteralEncoder.Encoder2 subCoder = this._literalEncoder.GetSubCoder((int)this.nowPos64, this._previousByte);
                  if (!Base.StateIsCharState(this._state)) {
                     byte matchByte = this._matchFinder.GetIndexByte(0 - this._repDistances[0] - 1 - this._additionalOffset);
                     subCoder.EncodeMatched(this._rangeEncoder, matchByte, curByte);
                  } else {
                     subCoder.Encode(this._rangeEncoder, curByte);
                  }

                  this._previousByte = curByte;
                  this._state = Base.StateUpdateChar(this._state);
               } else {
                  this._rangeEncoder.Encode(this._isMatch, complexState, 1);
                  int distance;
                  int i;
                  if (pos < 4) {
                     this._rangeEncoder.Encode(this._isRep, this._state, 1);
                     if (pos == 0) {
                        this._rangeEncoder.Encode(this._isRepG0, this._state, 0);
                        if (len == 1) {
                           this._rangeEncoder.Encode(this._isRep0Long, complexState, 0);
                        } else {
                           this._rangeEncoder.Encode(this._isRep0Long, complexState, 1);
                        }
                     } else {
                        this._rangeEncoder.Encode(this._isRepG0, this._state, 1);
                        if (pos == 1) {
                           this._rangeEncoder.Encode(this._isRepG1, this._state, 0);
                        } else {
                           this._rangeEncoder.Encode(this._isRepG1, this._state, 1);
                           this._rangeEncoder.Encode(this._isRepG2, this._state, pos - 2);
                        }
                     }

                     if (len == 1) {
                        this._state = Base.StateUpdateShortRep(this._state);
                     } else {
                        this._repMatchLenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                        this._state = Base.StateUpdateRep(this._state);
                     }

                     distance = this._repDistances[pos];
                     if (pos != 0) {
                        for(i = pos; i >= 1; --i) {
                           this._repDistances[i] = this._repDistances[i - 1];
                        }

                        this._repDistances[0] = distance;
                     }
                  } else {
                     this._rangeEncoder.Encode(this._isRep, this._state, 0);
                     this._state = Base.StateUpdateMatch(this._state);
                     this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                     pos -= 4;
                     distance = GetPosSlot(pos);
                     i = Base.GetLenToPosState(len);
                     this._posSlotEncoder[i].Encode(this._rangeEncoder, distance);
                     int i;
                     if (distance >= 4) {
                        int footerBits = (distance >> 1) - 1;
                        i = (2 | distance & 1) << footerBits;
                        int posReduced = pos - i;
                        if (distance < 14) {
                           BitTreeEncoder.ReverseEncode(this._posEncoders, i - distance - 1, this._rangeEncoder, footerBits, posReduced);
                        } else {
                           this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
                           this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
                           ++this._alignPriceCount;
                        }
                     }

                     for(i = 3; i >= 1; --i) {
                        this._repDistances[i] = this._repDistances[i - 1];
                     }

                     this._repDistances[0] = pos;
                     ++this._matchPriceCount;
                  }

                  this._previousByte = this._matchFinder.GetIndexByte(len - 1 - this._additionalOffset);
               }

               this._additionalOffset -= len;
               this.nowPos64 += (long)len;
               if (this._additionalOffset == 0) {
                  if (this._matchPriceCount >= 128) {
                     this.FillDistancesPrices();
                  }

                  if (this._alignPriceCount >= 16) {
                     this.FillAlignPrices();
                  }

                  inSize[0] = this.nowPos64;
                  outSize[0] = this._rangeEncoder.GetProcessedSizeAdd();
                  if (this._matchFinder.GetNumAvailableBytes() == 0) {
                     this.Flush((int)this.nowPos64);
                     return;
                  }

                  if (this.nowPos64 - progressPosValuePrev >= 4096L) {
                     this._finished = false;
                     finished[0] = false;
                     return;
                  }
               }
            }
         }
      }
   }

   void ReleaseMFStream() {
      if (this._matchFinder != null && this._needReleaseMFStream) {
         this._matchFinder.ReleaseStream();
         this._needReleaseMFStream = false;
      }

   }

   void SetOutStream(OutputStream outStream) {
      this._rangeEncoder.SetStream(outStream);
   }

   void ReleaseOutStream() {
      this._rangeEncoder.ReleaseStream();
   }

   void ReleaseStreams() {
      this.ReleaseMFStream();
      this.ReleaseOutStream();
   }

   void SetStreams(InputStream inStream, OutputStream outStream, long inSize, long outSize) {
      this._inStream = inStream;
      this._finished = false;
      this.Create();
      this.SetOutStream(outStream);
      this.Init();
      this.FillDistancesPrices();
      this.FillAlignPrices();
      this._lenEncoder.SetTableSize(this._numFastBytes + 1 - 2);
      this._lenEncoder.UpdateTables(1 << this._posStateBits);
      this._repMatchLenEncoder.SetTableSize(this._numFastBytes + 1 - 2);
      this._repMatchLenEncoder.UpdateTables(1 << this._posStateBits);
      this.nowPos64 = 0L;
   }

   public void Code(InputStream inStream, OutputStream outStream, long inSize, long outSize, ICodeProgress progress) throws IOException {
      this._needReleaseMFStream = false;

      try {
         this.SetStreams(inStream, outStream, inSize, outSize);

         while(true) {
            this.CodeOneBlock(this.processedInSize, this.processedOutSize, this.finished);
            if (this.finished[0]) {
               return;
            }

            if (progress != null) {
               progress.SetProgress(this.processedInSize[0], this.processedOutSize[0]);
            }
         }
      } finally {
         this.ReleaseStreams();
      }
   }

   public void WriteCoderProperties(OutputStream outStream) throws IOException {
      this.properties[0] = (byte)((this._posStateBits * 5 + this._numLiteralPosStateBits) * 9 + this._numLiteralContextBits);

      for(int i = 0; i < 4; ++i) {
         this.properties[1 + i] = (byte)(this._dictionarySize >> 8 * i);
      }

      outStream.write(this.properties, 0, 5);
   }

   void FillDistancesPrices() {
      int lenToPosState;
      int posSlot;
      int st;
      for(lenToPosState = 4; lenToPosState < 128; ++lenToPosState) {
         posSlot = GetPosSlot(lenToPosState);
         int footerBits = (posSlot >> 1) - 1;
         st = (2 | posSlot & 1) << footerBits;
         this.tempPrices[lenToPosState] = BitTreeEncoder.ReverseGetPrice(this._posEncoders, st - posSlot - 1, footerBits, lenToPosState - st);
      }

      for(lenToPosState = 0; lenToPosState < 4; ++lenToPosState) {
         BitTreeEncoder encoder = this._posSlotEncoder[lenToPosState];
         st = lenToPosState << 6;

         for(posSlot = 0; posSlot < this._distTableSize; ++posSlot) {
            this._posSlotPrices[st + posSlot] = encoder.GetPrice(posSlot);
         }

         for(posSlot = 14; posSlot < this._distTableSize; ++posSlot) {
            int[] var10000 = this._posSlotPrices;
            var10000[st + posSlot] += (posSlot >> 1) - 1 - 4 << 6;
         }

         int st2 = lenToPosState * 128;

         int i;
         for(i = 0; i < 4; ++i) {
            this._distancesPrices[st2 + i] = this._posSlotPrices[st + i];
         }

         while(i < 128) {
            this._distancesPrices[st2 + i] = this._posSlotPrices[st + GetPosSlot(i)] + this.tempPrices[i];
            ++i;
         }
      }

      this._matchPriceCount = 0;
   }

   void FillAlignPrices() {
      for(int i = 0; i < 16; ++i) {
         this._alignPrices[i] = this._posAlignEncoder.ReverseGetPrice(i);
      }

      this._alignPriceCount = 0;
   }

   public boolean SetAlgorithm(int algorithm) {
      return true;
   }

   public boolean SetDictionarySize(int dictionarySize) {
      int kDicLogSizeMaxCompress = 29;
      if (dictionarySize >= 1 && dictionarySize <= 1 << kDicLogSizeMaxCompress) {
         this._dictionarySize = dictionarySize;

         int dicLogSize;
         for(dicLogSize = 0; dictionarySize > 1 << dicLogSize; ++dicLogSize) {
         }

         this._distTableSize = dicLogSize * 2;
         return true;
      } else {
         return false;
      }
   }

   public boolean SetNumFastBytes(int numFastBytes) {
      if (numFastBytes >= 5 && numFastBytes <= 273) {
         this._numFastBytes = numFastBytes;
         return true;
      } else {
         return false;
      }
   }

   public boolean SetMatchFinder(int matchFinderIndex) {
      if (matchFinderIndex >= 0 && matchFinderIndex <= 2) {
         int matchFinderIndexPrev = this._matchFinderType;
         this._matchFinderType = matchFinderIndex;
         if (this._matchFinder != null && matchFinderIndexPrev != this._matchFinderType) {
            this._dictionarySizePrev = -1;
            this._matchFinder = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean SetLcLpPb(int lc, int lp, int pb) {
      if (lp >= 0 && lp <= 4 && lc >= 0 && lc <= 8 && pb >= 0 && pb <= 4) {
         this._numLiteralPosStateBits = lp;
         this._numLiteralContextBits = lc;
         this._posStateBits = pb;
         this._posStateMask = (1 << this._posStateBits) - 1;
         return true;
      } else {
         return false;
      }
   }

   public void SetEndMarkerMode(boolean endMarkerMode) {
      this._writeEndMark = endMarkerMode;
   }

   class LenEncoder {
      short[] _choice = new short[2];
      BitTreeEncoder[] _lowCoder = new BitTreeEncoder[16];
      BitTreeEncoder[] _midCoder = new BitTreeEncoder[16];
      BitTreeEncoder _highCoder = new BitTreeEncoder(8);

      public LenEncoder() {
         for(int posState = 0; posState < 16; ++posState) {
            this._lowCoder[posState] = new BitTreeEncoder(3);
            this._midCoder[posState] = new BitTreeEncoder(3);
         }

      }

      public void Init(int numPosStates) {
         com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._choice);

         for(int posState = 0; posState < numPosStates; ++posState) {
            this._lowCoder[posState].Init();
            this._midCoder[posState].Init();
         }

         this._highCoder.Init();
      }

      public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
         if (symbol < 8) {
            rangeEncoder.Encode(this._choice, 0, 0);
            this._lowCoder[posState].Encode(rangeEncoder, symbol);
         } else {
            symbol -= 8;
            rangeEncoder.Encode(this._choice, 0, 1);
            if (symbol < 8) {
               rangeEncoder.Encode(this._choice, 1, 0);
               this._midCoder[posState].Encode(rangeEncoder, symbol);
            } else {
               rangeEncoder.Encode(this._choice, 1, 1);
               this._highCoder.Encode(rangeEncoder, symbol - 8);
            }
         }

      }

      public void SetPrices(int posState, int numSymbols, int[] prices, int st) {
         int a0 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[0]);
         int a1 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[0]);
         int b0 = a1 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[1]);
         int b1 = a1 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[1]);
         int i = false;

         int ix;
         for(ix = 0; ix < 8; ++ix) {
            if (ix >= numSymbols) {
               return;
            }

            prices[st + ix] = a0 + this._lowCoder[posState].GetPrice(ix);
         }

         while(ix < 16) {
            if (ix >= numSymbols) {
               return;
            }

            prices[st + ix] = b0 + this._midCoder[posState].GetPrice(ix - 8);
            ++ix;
         }

         while(ix < numSymbols) {
            prices[st + ix] = b1 + this._highCoder.GetPrice(ix - 8 - 8);
            ++ix;
         }

      }
   }

   class LenPriceTableEncoder extends Encoder.LenEncoder {
      int[] _prices = new int[4352];
      int _tableSize;
      int[] _counters = new int[16];

      LenPriceTableEncoder() {
         super();
      }

      public void SetTableSize(int tableSize) {
         this._tableSize = tableSize;
      }

      public int GetPrice(int symbol, int posState) {
         return this._prices[posState * 272 + symbol];
      }

      void UpdateTable(int posState) {
         this.SetPrices(posState, this._tableSize, this._prices, posState * 272);
         this._counters[posState] = this._tableSize;
      }

      public void UpdateTables(int numPosStates) {
         for(int posState = 0; posState < numPosStates; ++posState) {
            this.UpdateTable(posState);
         }

      }

      public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
         super.Encode(rangeEncoder, symbol, posState);
         if (--this._counters[posState] == 0) {
            this.UpdateTable(posState);
         }

      }
   }

   class LiteralEncoder {
      Encoder.LiteralEncoder.Encoder2[] m_Coders;
      int m_NumPrevBits;
      int m_NumPosBits;
      int m_PosMask;

      public void Create(int numPosBits, int numPrevBits) {
         if (this.m_Coders == null || this.m_NumPrevBits != numPrevBits || this.m_NumPosBits != numPosBits) {
            this.m_NumPosBits = numPosBits;
            this.m_PosMask = (1 << numPosBits) - 1;
            this.m_NumPrevBits = numPrevBits;
            int numStates = 1 << this.m_NumPrevBits + this.m_NumPosBits;
            this.m_Coders = new Encoder.LiteralEncoder.Encoder2[numStates];

            for(int i = 0; i < numStates; ++i) {
               this.m_Coders[i] = new Encoder.LiteralEncoder.Encoder2();
            }

         }
      }

      public void Init() {
         int numStates = 1 << this.m_NumPrevBits + this.m_NumPosBits;

         for(int i = 0; i < numStates; ++i) {
            this.m_Coders[i].Init();
         }

      }

      public Encoder.LiteralEncoder.Encoder2 GetSubCoder(int pos, byte prevByte) {
         return this.m_Coders[((pos & this.m_PosMask) << this.m_NumPrevBits) + ((prevByte & 255) >>> 8 - this.m_NumPrevBits)];
      }

      class Encoder2 {
         short[] m_Encoders = new short[768];

         public void Init() {
            com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this.m_Encoders);
         }

         public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte symbol) throws IOException {
            int context = 1;

            for(int i = 7; i >= 0; --i) {
               int bit = symbol >> i & 1;
               rangeEncoder.Encode(this.m_Encoders, context, bit);
               context = context << 1 | bit;
            }

         }

         public void EncodeMatched(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte matchByte, byte symbol) throws IOException {
            int context = 1;
            boolean same = true;

            for(int i = 7; i >= 0; --i) {
               int bit = symbol >> i & 1;
               int state = context;
               if (same) {
                  int matchBit = matchByte >> i & 1;
                  state = context + (1 + matchBit << 8);
                  same = matchBit == bit;
               }

               rangeEncoder.Encode(this.m_Encoders, state, bit);
               context = context << 1 | bit;
            }

         }

         public int GetPrice(boolean matchMode, byte matchByte, byte symbol) {
            int price = 0;
            int context = 1;
            int i = 7;
            int matchBit;
            if (matchMode) {
               while(i >= 0) {
                  matchBit = matchByte >> i & 1;
                  int bit = symbol >> i & 1;
                  price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[(1 + matchBit << 8) + context], bit);
                  context = context << 1 | bit;
                  if (matchBit != bit) {
                     --i;
                     break;
                  }

                  --i;
               }
            }

            while(i >= 0) {
               matchBit = symbol >> i & 1;
               price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[context], matchBit);
               context = context << 1 | matchBit;
               --i;
            }

            return price;
         }
      }
   }

   class Optimal {
      public int State;
      public boolean Prev1IsChar;
      public boolean Prev2;
      public int PosPrev2;
      public int BackPrev2;
      public int Price;
      public int PosPrev;
      public int BackPrev;
      public int Backs0;
      public int Backs1;
      public int Backs2;
      public int Backs3;

      public void MakeAsChar() {
         this.BackPrev = -1;
         this.Prev1IsChar = false;
      }

      public void MakeAsShortRep() {
         this.BackPrev = 0;
         this.Prev1IsChar = false;
      }

      public boolean IsShortRep() {
         return this.BackPrev == 0;
      }
   }
}
