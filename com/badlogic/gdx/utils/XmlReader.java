package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

public class XmlReader {
   private final Array<XmlReader.Element> elements = new Array(8);
   private XmlReader.Element root;
   private XmlReader.Element current;
   private final StringBuilder textBuffer = new StringBuilder(64);
   private static final byte[] _xml_actions = init__xml_actions_0();
   private static final byte[] _xml_key_offsets = init__xml_key_offsets_0();
   private static final char[] _xml_trans_keys = init__xml_trans_keys_0();
   private static final byte[] _xml_single_lengths = init__xml_single_lengths_0();
   private static final byte[] _xml_range_lengths = init__xml_range_lengths_0();
   private static final short[] _xml_index_offsets = init__xml_index_offsets_0();
   private static final byte[] _xml_indicies = init__xml_indicies_0();
   private static final byte[] _xml_trans_targs = init__xml_trans_targs_0();
   private static final byte[] _xml_trans_actions = init__xml_trans_actions_0();
   static final int xml_start = 1;
   static final int xml_first_final = 34;
   static final int xml_error = 0;
   static final int xml_en_elementBody = 15;
   static final int xml_en_main = 1;

   public XmlReader.Element parse(String xml) {
      char[] data = xml.toCharArray();
      return this.parse(data, 0, data.length);
   }

   public XmlReader.Element parse(Reader reader) throws IOException {
      char[] data = new char[1024];
      int offset = 0;

      while(true) {
         int length = reader.read(data, offset, data.length - offset);
         if (length == -1) {
            return this.parse(data, 0, offset);
         }

         if (length == 0) {
            char[] newData = new char[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         } else {
            offset += length;
         }
      }
   }

   public XmlReader.Element parse(InputStream input) throws IOException {
      return this.parse((Reader)(new InputStreamReader(input, "ISO-8859-1")));
   }

   public XmlReader.Element parse(FileHandle file) throws IOException {
      try {
         return this.parse(file.read());
      } catch (Exception var3) {
         throw new SerializationException("Error parsing file: " + file, var3);
      }
   }

   public XmlReader.Element parse(char[] data, int offset, int length) {
      int p = offset;
      int pe = length;
      int s = 0;
      String attributeName = null;
      boolean hasBody = false;
      int cs = 1;
      int _trans = false;
      byte _goto_targ = 0;

      int i;
      label231:
      while(true) {
         while(true) {
            while(true) {
               label225:
               while(true) {
                  switch(_goto_targ) {
                  case 0:
                     if (p == pe) {
                        _goto_targ = 4;
                        continue;
                     }

                     if (cs == 0) {
                        _goto_targ = 5;
                        continue;
                     }
                  case 1:
                     break;
                  case 2:
                     break label225;
                  case 3:
                  case 4:
                  case 5:
                  default:
                     break label231;
                  }

                  int end;
                  int current;
                  label222: {
                     int _keys = _xml_key_offsets[cs];
                     i = _xml_index_offsets[cs];
                     int _klen = _xml_single_lengths[cs];
                     int _upper;
                     if (_klen > 0) {
                        end = _keys;
                        _upper = _keys + _klen - 1;

                        while(_upper >= end) {
                           current = end + (_upper - end >> 1);
                           if (data[p] < _xml_trans_keys[current]) {
                              _upper = current - 1;
                           } else {
                              if (data[p] <= _xml_trans_keys[current]) {
                                 i += current - _keys;
                                 break label222;
                              }

                              end = current + 1;
                           }
                        }

                        _keys += _klen;
                        i += _klen;
                     }

                     _klen = _xml_range_lengths[cs];
                     if (_klen > 0) {
                        end = _keys;
                        _upper = _keys + (_klen << 1) - 2;

                        while(true) {
                           if (_upper < end) {
                              i += _klen;
                              break;
                           }

                           current = end + (_upper - end >> 1 & -2);
                           if (data[p] < _xml_trans_keys[current]) {
                              _upper = current - 2;
                           } else {
                              if (data[p] <= _xml_trans_keys[current + 1]) {
                                 i += current - _keys >> 1;
                                 break;
                              }

                              end = current + 2;
                           }
                        }
                     }
                  }

                  int _trans = _xml_indicies[i];
                  cs = _xml_trans_targs[_trans];
                  if (_xml_trans_actions[_trans] == 0) {
                     break;
                  }

                  int _acts = _xml_trans_actions[_trans];
                  int var25 = _acts + 1;
                  int var13 = _xml_actions[_acts];

                  while(true) {
                     label192:
                     while(true) {
                        if (var13-- <= 0) {
                           break label225;
                        }

                        switch(_xml_actions[var25++]) {
                        case 0:
                           s = p;
                           break;
                        case 1:
                           char c = data[s];
                           if (c == '?' || c == '!') {
                              if (data[s + 1] == '[' && data[s + 2] == 'C' && data[s + 3] == 'D' && data[s + 4] == 'A' && data[s + 5] == 'T' && data[s + 6] == 'A' && data[s + 7] == '[') {
                                 s += 8;

                                 for(p = s + 2; data[p - 2] != ']' || data[p - 1] != ']' || data[p] != '>'; ++p) {
                                 }

                                 this.text(new String(data, s, p - s - 2));
                              } else {
                                 while(data[p] != '>') {
                                    ++p;
                                 }
                              }

                              cs = 15;
                              _goto_targ = 2;
                              continue label225;
                           }

                           hasBody = true;
                           this.open(new String(data, s, p - s));
                           break;
                        case 2:
                           hasBody = false;
                           this.close();
                           cs = 15;
                           _goto_targ = 2;
                           continue label225;
                        case 3:
                           this.close();
                           cs = 15;
                           _goto_targ = 2;
                           continue label225;
                        case 4:
                           if (hasBody) {
                              cs = 15;
                              _goto_targ = 2;
                              continue label225;
                           }
                           break;
                        case 5:
                           attributeName = new String(data, s, p - s);
                           break;
                        case 6:
                           this.attribute(attributeName, new String(data, s, p - s));
                           break;
                        case 7:
                           end = p;

                           label189:
                           while(end != s) {
                              switch(data[end - 1]) {
                              case '\t':
                              case '\n':
                              case '\r':
                              case ' ':
                                 --end;
                                 break;
                              default:
                                 break label189;
                              }
                           }

                           current = s;
                           boolean entityFound = false;

                           while(true) {
                              while(true) {
                                 do {
                                    if (current == end) {
                                       if (entityFound) {
                                          if (s < end) {
                                             this.textBuffer.append(data, s, end - s);
                                          }

                                          this.text(this.textBuffer.toString());
                                          this.textBuffer.setLength(0);
                                       } else {
                                          this.text(new String(data, s, end - s));
                                       }
                                       continue label192;
                                    }
                                 } while(data[current++] != '&');

                                 int entityStart = current;

                                 while(current != end) {
                                    if (data[current++] == ';') {
                                       this.textBuffer.append(data, s, entityStart - s - 1);
                                       String name = new String(data, entityStart, current - entityStart - 1);
                                       String value = this.entity(name);
                                       this.textBuffer.append(value != null ? value : name);
                                       s = current;
                                       entityFound = true;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (cs == 0) {
                  _goto_targ = 5;
               } else {
                  ++p;
                  if (p == pe) {
                     break label231;
                  }

                  _goto_targ = 1;
               }
            }
         }
      }

      if (p < pe) {
         int lineNumber = 1;

         for(i = 0; i < p; ++i) {
            if (data[i] == '\n') {
               ++lineNumber;
            }
         }

         throw new SerializationException("Error parsing XML on line " + lineNumber + " near: " + new String(data, p, Math.min(32, pe - p)));
      } else {
         XmlReader.Element root;
         if (this.elements.size != 0) {
            root = (XmlReader.Element)this.elements.peek();
            this.elements.clear();
            throw new SerializationException("Error parsing XML, unclosed element: " + root.getName());
         } else {
            root = this.root;
            this.root = null;
            return root;
         }
      }
   }

   private static byte[] init__xml_actions_0() {
      return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 2, 0, 6, 2, 1, 4, 2, 2, 4};
   }

   private static byte[] init__xml_key_offsets_0() {
      return new byte[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 36, 37, 42, 46, 50, 51, 52, 56, 57, 62, 67, 73, 79, 83, 88, 89, 90, 95, 99, 103, 104, 108, 109, 110, 111, 112, 115};
   }

   private static char[] init__xml_trans_keys_0() {
      return new char[]{' ', '<', '\t', '\r', ' ', '/', '>', '\t', '\r', ' ', '/', '>', '\t', '\r', ' ', '/', '=', '>', '\t', '\r', ' ', '/', '=', '>', '\t', '\r', ' ', '=', '\t', '\r', ' ', '"', '\'', '\t', '\r', '"', '"', ' ', '/', '>', '\t', '\r', ' ', '>', '\t', '\r', ' ', '>', '\t', '\r', '\'', '\'', ' ', '<', '\t', '\r', '<', ' ', '/', '>', '\t', '\r', ' ', '/', '>', '\t', '\r', ' ', '/', '=', '>', '\t', '\r', ' ', '/', '=', '>', '\t', '\r', ' ', '=', '\t', '\r', ' ', '"', '\'', '\t', '\r', '"', '"', ' ', '/', '>', '\t', '\r', ' ', '>', '\t', '\r', ' ', '>', '\t', '\r', '<', ' ', '/', '\t', '\r', '>', '>', '\'', '\'', ' ', '\t', '\r', '\u0000'};
   }

   private static byte[] init__xml_single_lengths_0() {
      return new byte[]{0, 2, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 1, 2, 1, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0};
   }

   private static byte[] init__xml_range_lengths_0() {
      return new byte[]{0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0};
   }

   private static short[] init__xml_index_offsets_0() {
      return new short[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 37, 39, 44, 48, 52, 54, 56, 60, 62, 67, 72, 78, 84, 88, 93, 95, 97, 102, 106, 110, 112, 116, 118, 120, 122, 124, 127};
   }

   private static byte[] init__xml_indicies_0() {
      return new byte[]{0, 2, 0, 1, 2, 1, 1, 2, 3, 5, 6, 7, 5, 4, 9, 10, 1, 11, 9, 8, 13, 1, 14, 1, 13, 12, 15, 16, 15, 1, 16, 17, 18, 16, 1, 20, 19, 22, 21, 9, 10, 11, 9, 1, 23, 24, 23, 1, 25, 11, 25, 1, 20, 26, 22, 27, 29, 30, 29, 28, 32, 31, 30, 34, 1, 30, 33, 36, 37, 38, 36, 35, 40, 41, 1, 42, 40, 39, 44, 1, 45, 1, 44, 43, 46, 47, 46, 1, 47, 48, 49, 47, 1, 51, 50, 53, 52, 40, 41, 42, 40, 1, 54, 55, 54, 1, 56, 42, 56, 1, 57, 1, 57, 34, 57, 1, 1, 58, 59, 58, 51, 60, 53, 61, 62, 62, 1, 1, 0};
   }

   private static byte[] init__xml_trans_targs_0() {
      return new byte[]{1, 0, 2, 3, 3, 4, 11, 34, 5, 4, 11, 34, 5, 6, 7, 6, 7, 8, 13, 9, 10, 9, 10, 12, 34, 12, 14, 14, 16, 15, 17, 16, 17, 18, 30, 18, 19, 26, 28, 20, 19, 26, 28, 20, 21, 22, 21, 22, 23, 32, 24, 25, 24, 25, 27, 28, 27, 29, 31, 35, 33, 33, 34};
   }

   private static byte[] init__xml_trans_actions_0() {
      return new byte[]{0, 0, 0, 1, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 1, 0, 1, 0, 0, 0, 15, 1, 0, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 0, 0, 7, 1, 0, 0};
   }

   protected void open(String name) {
      XmlReader.Element child = new XmlReader.Element(name, this.current);
      XmlReader.Element parent = this.current;
      if (parent != null) {
         parent.addChild(child);
      }

      this.elements.add(child);
      this.current = child;
   }

   protected void attribute(String name, String value) {
      this.current.setAttribute(name, value);
   }

   protected String entity(String name) {
      if (name.equals("lt")) {
         return "<";
      } else if (name.equals("gt")) {
         return ">";
      } else if (name.equals("amp")) {
         return "&";
      } else if (name.equals("apos")) {
         return "'";
      } else {
         return name.equals("quot") ? "\"" : null;
      }
   }

   protected void text(String text) {
      String existing = this.current.getText();
      this.current.setText(existing != null ? existing + text : text);
   }

   protected void close() {
      this.root = (XmlReader.Element)this.elements.pop();
      this.current = this.elements.size > 0 ? (XmlReader.Element)this.elements.peek() : null;
   }

   public static class Element {
      private final String name;
      private ObjectMap<String, String> attributes;
      private Array<XmlReader.Element> children;
      private String text;
      private XmlReader.Element parent;

      public Element(String name, XmlReader.Element parent) {
         this.name = name;
         this.parent = parent;
      }

      public String getName() {
         return this.name;
      }

      public ObjectMap<String, String> getAttributes() {
         return this.attributes;
      }

      public String getAttribute(String name) {
         if (this.attributes == null) {
            throw new GdxRuntimeException("Element " + name + " doesn't have attribute: " + name);
         } else {
            String value = (String)this.attributes.get(name);
            if (value == null) {
               throw new GdxRuntimeException("Element " + name + " doesn't have attribute: " + name);
            } else {
               return value;
            }
         }
      }

      public String getAttribute(String name, String defaultValue) {
         if (this.attributes == null) {
            return defaultValue;
         } else {
            String value = (String)this.attributes.get(name);
            return value == null ? defaultValue : value;
         }
      }

      public void setAttribute(String name, String value) {
         if (this.attributes == null) {
            this.attributes = new ObjectMap(8);
         }

         this.attributes.put(name, value);
      }

      public int getChildCount() {
         return this.children == null ? 0 : this.children.size;
      }

      public XmlReader.Element getChild(int i) {
         if (this.children == null) {
            throw new GdxRuntimeException("Element has no children: " + this.name);
         } else {
            return (XmlReader.Element)this.children.get(i);
         }
      }

      public void addChild(XmlReader.Element element) {
         if (this.children == null) {
            this.children = new Array(8);
         }

         this.children.add(element);
      }

      public String getText() {
         return this.text;
      }

      public void setText(String text) {
         this.text = text;
      }

      public void removeChild(int index) {
         if (this.children != null) {
            this.children.removeIndex(index);
         }

      }

      public void removeChild(XmlReader.Element child) {
         if (this.children != null) {
            this.children.removeValue(child, true);
         }

      }

      public void remove() {
         this.parent.removeChild(this);
      }

      public XmlReader.Element getParent() {
         return this.parent;
      }

      public String toString() {
         return this.toString("");
      }

      public String toString(String indent) {
         StringBuilder buffer = new StringBuilder(128);
         buffer.append(indent);
         buffer.append('<');
         buffer.append(this.name);
         if (this.attributes != null) {
            Iterator var4 = this.attributes.entries().iterator();

            while(var4.hasNext()) {
               ObjectMap.Entry<String, String> entry = (ObjectMap.Entry)var4.next();
               buffer.append(' ');
               buffer.append((String)entry.key);
               buffer.append("=\"");
               buffer.append((String)entry.value);
               buffer.append('"');
            }
         }

         if (this.children != null || this.text != null && this.text.length() != 0) {
            buffer.append(">\n");
            String childIndent = indent + '\t';
            if (this.text != null && this.text.length() > 0) {
               buffer.append(childIndent);
               buffer.append(this.text);
               buffer.append('\n');
            }

            if (this.children != null) {
               Iterator var5 = this.children.iterator();

               while(var5.hasNext()) {
                  XmlReader.Element child = (XmlReader.Element)var5.next();
                  buffer.append(child.toString(childIndent));
                  buffer.append('\n');
               }
            }

            buffer.append(indent);
            buffer.append("</");
            buffer.append(this.name);
            buffer.append('>');
         } else {
            buffer.append("/>");
         }

         return buffer.toString();
      }

      public XmlReader.Element getChildByName(String name) {
         if (this.children == null) {
            return null;
         } else {
            for(int i = 0; i < this.children.size; ++i) {
               XmlReader.Element element = (XmlReader.Element)this.children.get(i);
               if (element.name.equals(name)) {
                  return element;
               }
            }

            return null;
         }
      }

      public XmlReader.Element getChildByNameRecursive(String name) {
         if (this.children == null) {
            return null;
         } else {
            for(int i = 0; i < this.children.size; ++i) {
               XmlReader.Element element = (XmlReader.Element)this.children.get(i);
               if (element.name.equals(name)) {
                  return element;
               }

               XmlReader.Element found = element.getChildByNameRecursive(name);
               if (found != null) {
                  return found;
               }
            }

            return null;
         }
      }

      public Array<XmlReader.Element> getChildrenByName(String name) {
         Array<XmlReader.Element> result = new Array();
         if (this.children == null) {
            return result;
         } else {
            for(int i = 0; i < this.children.size; ++i) {
               XmlReader.Element child = (XmlReader.Element)this.children.get(i);
               if (child.name.equals(name)) {
                  result.add(child);
               }
            }

            return result;
         }
      }

      public Array<XmlReader.Element> getChildrenByNameRecursively(String name) {
         Array<XmlReader.Element> result = new Array();
         this.getChildrenByNameRecursively(name, result);
         return result;
      }

      private void getChildrenByNameRecursively(String name, Array<XmlReader.Element> result) {
         if (this.children != null) {
            for(int i = 0; i < this.children.size; ++i) {
               XmlReader.Element child = (XmlReader.Element)this.children.get(i);
               if (child.name.equals(name)) {
                  result.add(child);
               }

               child.getChildrenByNameRecursively(name, result);
            }

         }
      }

      public float getFloatAttribute(String name) {
         return Float.parseFloat(this.getAttribute(name));
      }

      public float getFloatAttribute(String name, float defaultValue) {
         String value = this.getAttribute(name, (String)null);
         return value == null ? defaultValue : Float.parseFloat(value);
      }

      public int getIntAttribute(String name) {
         return Integer.parseInt(this.getAttribute(name));
      }

      public int getIntAttribute(String name, int defaultValue) {
         String value = this.getAttribute(name, (String)null);
         return value == null ? defaultValue : Integer.parseInt(value);
      }

      public boolean getBooleanAttribute(String name) {
         return Boolean.parseBoolean(this.getAttribute(name));
      }

      public boolean getBooleanAttribute(String name, boolean defaultValue) {
         String value = this.getAttribute(name, (String)null);
         return value == null ? defaultValue : Boolean.parseBoolean(value);
      }

      public String get(String name) {
         String value = this.get(name, (String)null);
         if (value == null) {
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name);
         } else {
            return value;
         }
      }

      public String get(String name, String defaultValue) {
         if (this.attributes != null) {
            String value = (String)this.attributes.get(name);
            if (value != null) {
               return value;
            }
         }

         XmlReader.Element child = this.getChildByName(name);
         if (child == null) {
            return defaultValue;
         } else {
            String value = child.getText();
            return value == null ? defaultValue : value;
         }
      }

      public int getInt(String name) {
         String value = this.get(name, (String)null);
         if (value == null) {
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name);
         } else {
            return Integer.parseInt(value);
         }
      }

      public int getInt(String name, int defaultValue) {
         String value = this.get(name, (String)null);
         return value == null ? defaultValue : Integer.parseInt(value);
      }

      public float getFloat(String name) {
         String value = this.get(name, (String)null);
         if (value == null) {
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name);
         } else {
            return Float.parseFloat(value);
         }
      }

      public float getFloat(String name, float defaultValue) {
         String value = this.get(name, (String)null);
         return value == null ? defaultValue : Float.parseFloat(value);
      }

      public boolean getBoolean(String name) {
         String value = this.get(name, (String)null);
         if (value == null) {
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name);
         } else {
            return Boolean.parseBoolean(value);
         }
      }

      public boolean getBoolean(String name, boolean defaultValue) {
         String value = this.get(name, (String)null);
         return value == null ? defaultValue : Boolean.parseBoolean(value);
      }
   }
}
