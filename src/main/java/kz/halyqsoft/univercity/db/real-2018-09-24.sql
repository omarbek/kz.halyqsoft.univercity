INSERT INTO public.pdf_document (id, user_id, title, file_name, deleted, period, created) VALUES
  (nextval('s_pdf_document'), 2, 'Южно-Казахстанский педагогический университет', 'ИУПС рус.pdf', FALSE, 0,
   '2018-09-21 16:45:34.678000');
INSERT INTO public.pdf_document (id, user_id, title, file_name, deleted, period, created) VALUES
  (nextval('s_pdf_document'), 2, 'Оңтүстік Қазақстан педагогикалық университеті', 'ИУПС каз.pdf', FALSE, 0,
   '2018-09-21 12:08:58.485000');

INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Фамилия           $name           Имя    $surname
Отчество       $firstName
Факультет:  $faculty
Шифр и название специальности $educode
Язык обучения   русский
Телефон $phone            Е-mail $email
Постоянное  местожительство $address
Временное  местожительство $address
', 6, 0, 'Normal', 12, 96, 10.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Заведующий выпускающей кафедры
__________________  ______________
 (подпись)                               (Ф.И.О.)
«_____» ____________________201_ г.
', 303, 57, 'Bold', 12, 96, 5.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Студента (ки)   $fio
Шифр и наименование специальности $educode
Курс $course язык обучения $language
Индивидуальный номер $code




', 6, 0, 'Normal', 12, 96, 4.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'ИНДИВИДУАЛЬНЫЙ УЧЕБНЫЙ ПЛАН
НА 2017/2018 У.Г.
', 0, 99, 'Bold', 12, 96, 3.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Шымкент – 2018 г.
', 0, 99, 'Bold', 12, 96, 6.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '$rus', 6, 0, 'Normal', 12, 96, 12.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '  «УТВЕРЖДАЮ»
Декан  факультета
________________  ______________
(подпись)                        (Ф.И.О.)
«___» __________________ 201_ г.
', 303, 54, 'Bold', 12, 96, 2.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Ф 08.02-05', 408, -45, 'Bold', 12, 96, 1.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '.', 6, 0, 'Normal', 12, 96, 11.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'ENROLLMENT
', 0, 69, 'Bold', 12, 96, 7.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'ЗАПИСЬ   НА ДИСЦИПЛИНУ
', 0, 0, 'Normal', 12, 96, 8.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'СТУДЕНТ:', 6, 0, 'Italic', 12, 96, 9.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Фамилиясы       $surname              Аты                    $name
Әкесінің аты     $firstName
Факультет         $speciality
Мамандық шифры мен атауы  $educode
Білім алатын тілі.    қазақша
Телефон          $phone                   Е-mail                 $email
Тұрақты мекен-жайы  $address
Уақытша тұратын жері   $address2
', 6, 0, 'Normal', 12, 97, 10.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Ф 08.02-05
', 408, -45, 'Bold', 12, 97, 1.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'СТУДЕНТ:
', 6, 0, 'Italic', 12, 97, 9.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'ПӘНГЕ  ТІРКЕУ
', 0, 0, 'Normal', 12, 97, 8.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'ENROLLMENT', 0, 81, 'Bold', 12, 97, 7.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Шымкент – 2018 ж.
', 0, 99, 'Bold', 12, 97, 6.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Бітіруші мамандықтың кафедра меңгерушісі

__________________  ______________
(қолы)                               (Т.А.Ә.)
«_____» ____________________201_ ж.

', 300, 75, 'Bold', 12, 97, 5.00, FALSE, TRUE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'Студент $fio
Мамандық шифры мен атауы $educode
Курсы $course оқу тілі $language
Жеке номері $code
', 0, 33, 'Bold', 12, 97, 4.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), 'СТУДЕНТТІҢ ЖЕКЕ ОҚУ ЖОСПАРЫ
2018-2019 ОҚУ ЖЫЛЫНА АРНАЛҒАН
', 0, 87, 'Bold', 12, 97, 3.00, TRUE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '$table', 6, 0, 'Normal', 12, 97, 14.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '.', 6, 0, 'Normal', 12, 97, 13.00, FALSE, FALSE, FALSE);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom)
VALUES (nextval('s_pdf_property'), '«Бекітемін»
Факультет деканы
________________  ______________
(қолы)                        (Т.А.Ә.)
«___» __________________ 201_ ж.
', 300, 54, 'Bold', 12, 97, 2.00, FALSE, TRUE, FALSE);