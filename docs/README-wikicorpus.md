Ngữ liệu Wikipedia tiếng Việt vào ngày 01.01.2014, đã loại bỏ các ký tự đặc 
biệt và tách từ tố. Sau khi giải nén, mỗi tệp gồm nhiều tài liệu viết liền 
nhau không có ký hiệu đánh dấu. Mỗi dòng chứa một câu, không có dòng trống.
Trong câu, các từ tố (ví dụ: âm tiết, số, dấu câu) viết cách nhau ít nhất một
khoảng trắng.

== Phương pháp ==

1. Tải ngữ liệu Wikipedia tiếng Việt chưa tách từ tố 
(https://www.mediafire.com/folder/5ado02uckf2t3/viwiki-text)
2. Xóa ký hiệu đặc biệt (sed '/^<.*>/d'), "magic word" (sed 's/__[[:alpha:]]\+__//g;')
3. Phát hiện câu theo mô tả ở đây: http://xltiengviet.wikia.com/wiki/Ph%C3%A1t_hi%E1%BB%87n_c%C3%A2u#Ph.C3.A1t_hi.E1.BB.87n_c.C3.A2u_b.E1.BA.B1ng_OpenNLP
4. Xóa dòng trống (sed '/^\s*$/d')

== Vấn đề đã biết ==

Xem https://www.mediafire.com/folder/5ado02uckf2t3/viwiki-text.

== Tác giả ==

Lê Ngọc Minh <ngocminh.oss@gmail.com>

== Bản quyền ==

CC BY-SA (http://creativecommons.org/licenses/by-sa/3.0/)
