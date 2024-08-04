package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonReader implements BaseJsonReader {
   private static final byte[] _json_actions = init__json_actions_0();
   private static final short[] _json_key_offsets = init__json_key_offsets_0();
   private static final char[] _json_trans_keys = init__json_trans_keys_0();
   private static final byte[] _json_single_lengths = init__json_single_lengths_0();
   private static final byte[] _json_range_lengths = init__json_range_lengths_0();
   private static final short[] _json_index_offsets = init__json_index_offsets_0();
   private static final byte[] _json_trans_targs = init__json_trans_targs_0();
   private static final byte[] _json_trans_actions = init__json_trans_actions_0();
   private static final byte[] _json_eof_actions = init__json_eof_actions_0();
   static final int json_start = 1;
   static final int json_first_final = 72;
   static final int json_error = 0;
   static final int json_en_object = 8;
   static final int json_en_array = 46;
   static final int json_en_main = 1;
   private final Array<JsonValue> elements = new Array(8);
   private final Array<JsonValue> lastChild = new Array(8);
   private JsonValue root;
   private JsonValue current;

   public JsonValue parse(String json) {
      char[] data = json.toCharArray();
      return this.parse(data, 0, data.length);
   }

   public JsonValue parse(Reader reader) {
      try {
         char[] data = new char[1024];
         int offset = 0;

         while(true) {
            int length = reader.read(data, offset, data.length - offset);
            if (length == -1) {
               JsonValue var7 = this.parse(data, 0, offset);
               return var7;
            }

            if (length == 0) {
               char[] newData = new char[data.length * 2];
               System.arraycopy(data, 0, newData, 0, data.length);
               data = newData;
            } else {
               offset += length;
            }
         }
      } catch (IOException var14) {
         throw new SerializationException(var14);
      } finally {
         try {
            reader.close();
         } catch (IOException var13) {
         }

      }
   }

   public JsonValue parse(InputStream input) {
      try {
         return this.parse((Reader)(new InputStreamReader(input, "ISO-8859-1")));
      } catch (IOException var3) {
         throw new SerializationException(var3);
      }
   }

   public JsonValue parse(FileHandle file) {
      try {
         return this.parse(file.read());
      } catch (Exception var3) {
         throw new SerializationException("Error parsing file: " + file, var3);
      }
   }

   public JsonValue parse(char[] data, int offset, int length) {
      int p = offset;
      int pe = length;
      int eof = length;
      int top = false;
      int[] stack = new int[4];
      int s = 0;
      Array<String> names = new Array(8);
      boolean needsUnescape = false;
      boolean discardBuffer = false;
      RuntimeException parseRuntimeEx = null;
      boolean debug = false;
      if (debug) {
         System.out.println();
      }

      int _trans;
      int i;
      try {
         label321: {
            int cs = 1;
            int top = 0;
            int _trans = false;
            byte _goto_targ = 0;

            int _lower;
            int _mid;
            label313:
            while(true) {
               switch(_goto_targ) {
               case 0:
                  if (p == pe) {
                     _goto_targ = 4;
                     break;
                  } else if (cs == 0) {
                     _goto_targ = 5;
                     break;
                  }
               case 1:
                  label322: {
                     int _keys = _json_key_offsets[cs];
                     _trans = _json_index_offsets[cs];
                     int _klen = _json_single_lengths[cs];
                     int _upper;
                     if (_klen > 0) {
                        _lower = _keys;
                        _upper = _keys + _klen - 1;

                        while(_upper >= _lower) {
                           _mid = _lower + (_upper - _lower >> 1);
                           if (data[p] < _json_trans_keys[_mid]) {
                              _upper = _mid - 1;
                           } else {
                              if (data[p] <= _json_trans_keys[_mid]) {
                                 _trans += _mid - _keys;
                                 break label322;
                              }

                              _lower = _mid + 1;
                           }
                        }

                        _keys += _klen;
                        _trans += _klen;
                     }

                     _klen = _json_range_lengths[cs];
                     if (_klen > 0) {
                        _lower = _keys;
                        _upper = _keys + (_klen << 1) - 2;

                        while(true) {
                           if (_upper < _lower) {
                              _trans += _klen;
                              break;
                           }

                           _mid = _lower + (_upper - _lower >> 1 & -2);
                           if (data[p] < _json_trans_keys[_mid]) {
                              _upper = _mid - 2;
                           } else {
                              if (data[p] <= _json_trans_keys[_mid + 1]) {
                                 _trans += _mid - _keys >> 1;
                                 break;
                              }

                              _lower = _mid + 2;
                           }
                        }
                     }
                  }

                  cs = _json_trans_targs[_trans];
                  if (_json_trans_actions[_trans] != 0) {
                     int _acts = _json_trans_actions[_trans];
                     i = _acts + 1;
                     int var19 = _json_actions[_acts];

                     while(var19-- > 0) {
                        String value;
                        int[] newStack;
                        String name;
                        switch(_json_actions[i++]) {
                        case 0:
                           s = p;
                           needsUnescape = false;
                           discardBuffer = false;
                           break;
                        case 1:
                           needsUnescape = true;
                           break;
                        case 2:
                           value = new String(data, s, p - s);
                           s = p;
                           if (needsUnescape) {
                              value = this.unescape(value);
                           }

                           if (debug) {
                              System.out.println("name: " + value);
                           }

                           names.add(value);
                           break;
                        case 3:
                           if (!discardBuffer) {
                              value = new String(data, s, p - s);
                              s = p;
                              if (needsUnescape) {
                                 value = this.unescape(value);
                              }

                              name = names.size > 0 ? (String)names.pop() : null;
                              if (debug) {
                                 System.out.println("string: " + name + "=" + value);
                              }

                              this.string(name, value);
                           }
                           break;
                        case 4:
                           value = new String(data, s, p - s);
                           s = p;
                           name = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("double: " + name + "=" + Double.parseDouble(value));
                           }

                           this.number(name, Double.parseDouble(value));
                           break;
                        case 5:
                           value = new String(data, s, p - s);
                           s = p;
                           name = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("long: " + name + "=" + Long.parseLong(value));
                           }

                           this.number(name, Long.parseLong(value));
                           break;
                        case 6:
                           value = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("boolean: " + value + "=true");
                           }

                           this.bool(value, true);
                           discardBuffer = true;
                           break;
                        case 7:
                           value = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("boolean: " + value + "=false");
                           }

                           this.bool(value, false);
                           discardBuffer = true;
                           break;
                        case 8:
                           value = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("null: " + value);
                           }

                           this.string(value, (String)null);
                           discardBuffer = true;
                           break;
                        case 9:
                           value = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("startObject: " + value);
                           }

                           this.startObject(value);
                           if (top == stack.length) {
                              newStack = new int[stack.length * 2];
                              System.arraycopy(stack, 0, newStack, 0, stack.length);
                              stack = newStack;
                           }

                           stack[top++] = cs;
                           cs = 8;
                           _goto_targ = 2;
                           continue label313;
                        case 10:
                           if (debug) {
                              System.out.println("endObject");
                           }

                           this.pop();
                           --top;
                           cs = stack[top];
                           _goto_targ = 2;
                           continue label313;
                        case 11:
                           value = names.size > 0 ? (String)names.pop() : null;
                           if (debug) {
                              System.out.println("startArray: " + value);
                           }

                           this.startArray(value);
                           if (top == stack.length) {
                              newStack = new int[stack.length * 2];
                              System.arraycopy(stack, 0, newStack, 0, stack.length);
                              stack = newStack;
                           }

                           stack[top++] = cs;
                           cs = 46;
                           _goto_targ = 2;
                           continue label313;
                        case 12:
                           if (debug) {
                              System.out.println("endArray");
                           }

                           this.pop();
                           --top;
                           cs = stack[top];
                           _goto_targ = 2;
                           continue label313;
                        }
                     }
                  }
               case 2:
                  if (cs == 0) {
                     _goto_targ = 5;
                     break;
                  }

                  ++p;
                  if (p == pe) {
                     break label313;
                  }

                  _goto_targ = 1;
                  break;
               case 3:
               case 5:
               default:
                  break label321;
               case 4:
                  break label313;
               }
            }

            if (p == eof) {
               int __acts = _json_eof_actions[cs];
               _lower = __acts + 1;
               _mid = _json_actions[__acts];

               while(_mid-- > 0) {
                  String name;
                  String value;
                  switch(_json_actions[_lower++]) {
                  case 3:
                     if (!discardBuffer) {
                        value = new String(data, s, p - s);
                        s = p;
                        if (needsUnescape) {
                           value = this.unescape(value);
                        }

                        name = names.size > 0 ? (String)names.pop() : null;
                        if (debug) {
                           System.out.println("string: " + name + "=" + value);
                        }

                        this.string(name, value);
                     }
                     break;
                  case 4:
                     value = new String(data, s, p - s);
                     s = p;
                     name = names.size > 0 ? (String)names.pop() : null;
                     if (debug) {
                        System.out.println("double: " + name + "=" + Double.parseDouble(value));
                     }

                     this.number(name, Double.parseDouble(value));
                     break;
                  case 5:
                     value = new String(data, s, p - s);
                     s = p;
                     name = names.size > 0 ? (String)names.pop() : null;
                     if (debug) {
                        System.out.println("long: " + name + "=" + Long.parseLong(value));
                     }

                     this.number(name, Long.parseLong(value));
                     break;
                  case 6:
                     value = names.size > 0 ? (String)names.pop() : null;
                     if (debug) {
                        System.out.println("boolean: " + value + "=true");
                     }

                     this.bool(value, true);
                     discardBuffer = true;
                     break;
                  case 7:
                     value = names.size > 0 ? (String)names.pop() : null;
                     if (debug) {
                        System.out.println("boolean: " + value + "=false");
                     }

                     this.bool(value, false);
                     discardBuffer = true;
                     break;
                  case 8:
                     value = names.size > 0 ? (String)names.pop() : null;
                     if (debug) {
                        System.out.println("null: " + value);
                     }

                     this.string(value, (String)null);
                     discardBuffer = true;
                  }
               }
            }
         }
      } catch (RuntimeException var26) {
         parseRuntimeEx = var26;
      }

      JsonValue root = this.root;
      this.root = null;
      this.current = null;
      this.lastChild.clear();
      if (p < pe) {
         _trans = 1;

         for(i = 0; i < p; ++i) {
            if (data[i] == '\n') {
               ++_trans;
            }
         }

         throw new SerializationException("Error parsing JSON on line " + _trans + " near: " + new String(data, p, pe - p), parseRuntimeEx);
      } else if (this.elements.size != 0) {
         JsonValue element = (JsonValue)this.elements.peek();
         this.elements.clear();
         if (element != null && element.isObject()) {
            throw new SerializationException("Error parsing JSON, unmatched brace.");
         } else {
            throw new SerializationException("Error parsing JSON, unmatched bracket.");
         }
      } else if (parseRuntimeEx != null) {
         throw new SerializationException("Error parsing JSON: " + new String(data), parseRuntimeEx);
      } else {
         return root;
      }
   }

   private static byte[] init__json_actions_0() {
      return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 9, 1, 10, 1, 11, 1, 12, 2, 0, 2, 2, 0, 3, 2, 3, 10, 2, 3, 12, 2, 4, 10, 2, 4, 12, 2, 5, 10, 2, 5, 12, 2, 6, 3, 2, 7, 3, 2, 8, 3, 3, 6, 3, 10, 3, 6, 3, 12, 3, 7, 3, 10, 3, 7, 3, 12, 3, 8, 3, 10, 3, 8, 3, 12};
   }

   private static short[] init__json_key_offsets_0() {
      return new short[]{0, 0, 18, 20, 22, 31, 33, 37, 39, 54, 56, 58, 62, 80, 82, 84, 89, 103, 110, 112, 115, 123, 127, 129, 135, 144, 151, 153, 161, 170, 174, 176, 183, 191, 199, 207, 215, 222, 230, 238, 246, 253, 261, 269, 277, 284, 293, 313, 315, 317, 322, 341, 348, 350, 358, 367, 371, 373, 380, 388, 396, 404, 412, 419, 427, 435, 443, 450, 458, 466, 474, 481, 490, 493, 500, 506, 513, 518, 526, 534, 542, 550, 557, 565, 573, 581, 588, 596, 604, 612, 619, 619};
   }

   private static char[] init__json_trans_keys_0() {
      return new char[]{' ', '"', '$', '-', '[', '_', 'f', 'n', 't', '{', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', '"', '\\', '"', '\\', '"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', '0', '9', '+', '-', '0', '9', '0', '9', ' ', '"', '$', ',', '-', '_', '}', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', '"', '\\', '"', '\\', ' ', ':', '\t', '\r', ' ', '"', '$', '-', '[', '_', 'f', 'n', 't', '{', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', '"', '\\', '"', '\\', ' ', ',', '}', '\t', '\r', ' ', '"', '$', '-', '_', '}', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', ' ', ',', ':', ']', '}', '\t', '\r', '0', '9', '.', '0', '9', ' ', ':', 'E', 'e', '\t', '\r', '0', '9', '+', '-', '0', '9', '0', '9', ' ', ':', '\t', '\r', '0', '9', '"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', ',', ':', ']', '}', '\t', '\r', '0', '9', ' ', ',', '.', '}', '\t', '\r', '0', '9', ' ', ',', 'E', 'e', '}', '\t', '\r', '0', '9', '+', '-', '0', '9', '0', '9', ' ', ',', '}', '\t', '\r', '0', '9', ' ', ',', ':', ']', 'a', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 's', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'r', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', '"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', '"', '$', ',', '-', '[', ']', '_', 'f', 'n', 't', '{', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', '"', '\\', '"', '\\', ' ', ',', ']', '\t', '\r', ' ', '"', '$', '-', '[', ']', '_', 'f', 'n', 't', '{', '\t', '\r', '0', '9', 'A', 'Z', 'a', 'z', ' ', ',', ':', ']', '}', '\t', '\r', '0', '9', ' ', ',', '.', ']', '\t', '\r', '0', '9', ' ', ',', 'E', ']', 'e', '\t', '\r', '0', '9', '+', '-', '0', '9', '0', '9', ' ', ',', ']', '\t', '\r', '0', '9', ' ', ',', ':', ']', 'a', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 's', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'r', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', '"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', '.', '\t', '\r', '0', '9', ' ', 'E', 'e', '\t', '\r', '0', '9', ' ', '\t', '\r', '0', '9', ' ', ',', ':', ']', 'a', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 's', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', 'l', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', ' ', ',', ':', ']', 'r', '}', '\t', '\r', ' ', ',', ':', ']', 'u', '}', '\t', '\r', ' ', ',', ':', ']', 'e', '}', '\t', '\r', ' ', ',', ':', ']', '}', '\t', '\r', '\u0000'};
   }

   private static byte[] init__json_single_lengths_0() {
      return new byte[]{0, 10, 2, 2, 7, 0, 2, 0, 7, 2, 2, 2, 10, 2, 2, 3, 6, 5, 0, 1, 4, 2, 0, 2, 7, 5, 0, 4, 5, 2, 0, 3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 7, 12, 2, 2, 3, 11, 5, 0, 4, 5, 2, 0, 3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 7, 1, 5, 2, 3, 1, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 0, 0};
   }

   private static byte[] init__json_range_lengths_0() {
      return new byte[]{0, 4, 0, 0, 1, 1, 1, 1, 4, 0, 0, 1, 4, 0, 0, 1, 4, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 0, 0, 1, 4, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
   }

   private static short[] init__json_index_offsets_0() {
      return new short[]{0, 0, 15, 18, 21, 30, 32, 36, 38, 50, 53, 56, 60, 75, 78, 81, 86, 97, 104, 106, 109, 116, 120, 122, 127, 136, 143, 145, 152, 160, 164, 166, 172, 180, 188, 196, 204, 211, 219, 227, 235, 242, 250, 258, 266, 273, 282, 299, 302, 305, 310, 326, 333, 335, 342, 350, 354, 356, 362, 370, 378, 386, 394, 401, 409, 417, 425, 432, 440, 448, 456, 463, 472, 475, 482, 487, 493, 497, 505, 513, 521, 529, 536, 544, 552, 560, 567, 575, 583, 591, 598, 599};
   }

   private static byte[] init__json_trans_targs_0() {
      return new byte[]{1, 2, 73, 5, 72, 73, 77, 82, 86, 72, 1, 74, 73, 73, 0, 72, 4, 3, 72, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 74, 0, 7, 7, 76, 0, 76, 0, 8, 9, 17, 16, 18, 17, 90, 8, 17, 17, 17, 0, 11, 45, 10, 11, 45, 10, 11, 12, 11, 0, 12, 13, 25, 26, 15, 25, 32, 37, 41, 15, 12, 27, 25, 25, 0, 15, 24, 14, 15, 24, 14, 15, 16, 90, 15, 0, 16, 9, 17, 18, 17, 90, 16, 17, 17, 17, 0, 11, 0, 12, 0, 0, 11, 17, 19, 0, 20, 19, 0, 11, 12, 21, 21, 11, 20, 0, 22, 22, 23, 0, 23, 0, 11, 12, 11, 23, 0, 14, 14, 14, 14, 14, 14, 14, 14, 0, 15, 16, 0, 0, 90, 15, 25, 27, 0, 15, 16, 28, 90, 15, 27, 0, 15, 16, 29, 29, 90, 15, 28, 0, 30, 30, 31, 0, 31, 0, 15, 16, 90, 15, 31, 0, 15, 16, 0, 0, 33, 90, 15, 25, 15, 16, 0, 0, 34, 90, 15, 25, 15, 16, 0, 0, 35, 90, 15, 25, 15, 16, 0, 0, 36, 90, 15, 25, 15, 16, 0, 0, 90, 15, 25, 15, 16, 0, 0, 38, 90, 15, 25, 15, 16, 0, 0, 39, 90, 15, 25, 15, 16, 0, 0, 40, 90, 15, 25, 15, 16, 0, 0, 90, 15, 25, 15, 16, 0, 0, 42, 90, 15, 25, 15, 16, 0, 0, 43, 90, 15, 25, 15, 16, 0, 0, 44, 90, 15, 25, 15, 16, 0, 0, 90, 15, 25, 10, 10, 10, 10, 10, 10, 10, 10, 0, 46, 47, 51, 50, 52, 49, 91, 51, 58, 63, 67, 49, 46, 53, 51, 51, 0, 49, 71, 48, 49, 71, 48, 49, 50, 91, 49, 0, 50, 47, 51, 52, 49, 91, 51, 58, 63, 67, 49, 50, 53, 51, 51, 0, 49, 50, 0, 91, 0, 49, 51, 53, 0, 49, 50, 54, 91, 49, 53, 0, 49, 50, 55, 91, 55, 49, 54, 0, 56, 56, 57, 0, 57, 0, 49, 50, 91, 49, 57, 0, 49, 50, 0, 91, 59, 0, 49, 51, 49, 50, 0, 91, 60, 0, 49, 51, 49, 50, 0, 91, 61, 0, 49, 51, 49, 50, 0, 91, 62, 0, 49, 51, 49, 50, 0, 91, 0, 49, 51, 49, 50, 0, 91, 64, 0, 49, 51, 49, 50, 0, 91, 65, 0, 49, 51, 49, 50, 0, 91, 66, 0, 49, 51, 49, 50, 0, 91, 0, 49, 51, 49, 50, 0, 91, 68, 0, 49, 51, 49, 50, 0, 91, 69, 0, 49, 51, 49, 50, 0, 91, 70, 0, 49, 51, 49, 50, 0, 91, 0, 49, 51, 48, 48, 48, 48, 48, 48, 48, 48, 0, 72, 72, 0, 72, 0, 0, 0, 0, 72, 73, 72, 75, 72, 74, 0, 72, 6, 6, 72, 75, 0, 72, 72, 76, 0, 72, 0, 0, 0, 78, 0, 72, 73, 72, 0, 0, 0, 79, 0, 72, 73, 72, 0, 0, 0, 80, 0, 72, 73, 72, 0, 0, 0, 81, 0, 72, 73, 72, 0, 0, 0, 0, 72, 73, 72, 0, 0, 0, 83, 0, 72, 73, 72, 0, 0, 0, 84, 0, 72, 73, 72, 0, 0, 0, 85, 0, 72, 73, 72, 0, 0, 0, 0, 72, 73, 72, 0, 0, 0, 87, 0, 72, 73, 72, 0, 0, 0, 88, 0, 72, 73, 72, 0, 0, 0, 89, 0, 72, 73, 72, 0, 0, 0, 0, 72, 73, 0, 0, 0};
   }

   private static byte[] init__json_trans_actions_0() {
      return new byte[]{0, 0, 1, 1, 17, 1, 1, 1, 1, 13, 0, 1, 1, 1, 0, 24, 1, 1, 7, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 15, 0, 1, 1, 1, 0, 21, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 17, 1, 1, 1, 1, 13, 0, 1, 1, 1, 0, 24, 1, 1, 7, 0, 0, 0, 0, 15, 0, 0, 0, 0, 1, 1, 1, 15, 0, 1, 1, 1, 0, 5, 0, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 7, 7, 0, 0, 27, 7, 0, 0, 0, 11, 11, 0, 39, 11, 0, 0, 9, 9, 0, 0, 33, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 33, 9, 0, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 48, 48, 0, 0, 62, 48, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 51, 51, 0, 0, 70, 51, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 7, 7, 0, 0, 0, 27, 7, 0, 45, 45, 0, 0, 54, 45, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 1, 0, 1, 17, 19, 1, 1, 1, 1, 13, 0, 1, 1, 1, 0, 24, 1, 1, 7, 0, 0, 0, 0, 19, 0, 0, 0, 0, 1, 1, 17, 19, 1, 1, 1, 1, 13, 0, 1, 1, 1, 0, 7, 7, 0, 30, 0, 7, 0, 0, 0, 11, 11, 0, 42, 11, 0, 0, 9, 9, 0, 36, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 36, 9, 0, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 48, 48, 0, 66, 0, 48, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 51, 51, 0, 74, 0, 51, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 7, 7, 0, 30, 0, 0, 7, 0, 45, 45, 0, 58, 0, 45, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 7, 0, 0, 0, 0, 7, 0, 11, 0, 11, 0, 0, 9, 0, 0, 9, 0, 0, 9, 9, 0, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 48, 0, 0, 0, 0, 48, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 51, 0, 0, 0, 0, 51, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 45, 0, 0, 0, 0, 45, 0, 0, 0, 0};
   }

   private static byte[] init__json_eof_actions_0() {
      return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 11, 9, 9, 7, 7, 7, 7, 48, 7, 7, 7, 51, 7, 7, 7, 45, 0, 0};
   }

   private void addChild(String name, JsonValue child) {
      child.setName(name);
      if (this.current == null) {
         this.current = child;
         this.root = child;
      } else if (!this.current.isArray() && !this.current.isObject()) {
         this.root = this.current;
      } else {
         if (this.current.size == 0) {
            this.current.child = child;
         } else {
            ((JsonValue)this.lastChild.pop()).next = child;
         }

         this.lastChild.add(child);
         ++this.current.size;
      }

   }

   protected void startObject(String name) {
      JsonValue value = new JsonValue(JsonValue.ValueType.object);
      if (this.current != null) {
         this.addChild(name, value);
      }

      this.elements.add(value);
      this.current = value;
   }

   protected void startArray(String name) {
      JsonValue value = new JsonValue(JsonValue.ValueType.array);
      if (this.current != null) {
         this.addChild(name, value);
      }

      this.elements.add(value);
      this.current = value;
   }

   protected void pop() {
      this.root = (JsonValue)this.elements.pop();
      if (this.current.size > 0) {
         this.lastChild.pop();
      }

      this.current = this.elements.size > 0 ? (JsonValue)this.elements.peek() : null;
   }

   protected void string(String name, String value) {
      this.addChild(name, new JsonValue(value));
   }

   protected void number(String name, double value) {
      this.addChild(name, new JsonValue(value));
   }

   protected void number(String name, long value) {
      this.addChild(name, new JsonValue(value));
   }

   protected void bool(String name, boolean value) {
      this.addChild(name, new JsonValue(value));
   }

   private String unescape(String value) {
      int length = value.length();
      StringBuilder buffer = new StringBuilder(length + 16);
      int i = 0;

      while(i < length) {
         char c = value.charAt(i++);
         if (c != '\\') {
            buffer.append(c);
         } else {
            if (i == length) {
               break;
            }

            c = value.charAt(i++);
            if (c == 'u') {
               buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
               i += 4;
            } else {
               switch(c) {
               case '"':
               case '/':
               case '\\':
                  break;
               case 'b':
                  c = '\b';
                  break;
               case 'f':
                  c = '\f';
                  break;
               case 'n':
                  c = '\n';
                  break;
               case 'r':
                  c = '\r';
                  break;
               case 't':
                  c = '\t';
                  break;
               default:
                  throw new SerializationException("Illegal escaped character: \\" + c);
               }

               buffer.append(c);
            }
         }
      }

      return buffer.toString();
   }
}
