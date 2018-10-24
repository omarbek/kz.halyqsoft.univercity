
INSERT INTO public.pdf_document (id, user_id, title, file_name, deleted, period, created) VALUES (178, 2, ' ', 'Анкета.pdf', false, 0, '2018-10-17 16:39:19.867000');
INSERT INTO public.pdf_document (id, user_id, title, file_name, deleted, period, created) VALUES (179, 2, ' ', 'Заявление.pdf', false, 0, '2018-10-18 14:53:39.572000');


INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1134, 'АВТОНОМИЯ НЕКОММЕРЧЕСКАЯ ОРГАНИЗАЦИЯ ВЫСШЕГО ОБРАЗОВАНИЯ
"ИНСТИТУТ ФИНАНСОВ, ЭКОНОМИКИ И ПРАВА ОФИЦЕРОВ ЗАПАСА"
(АНО ВО "ИФЭП ОЗ")', 0, 0, 'Bold', 12, 178, 1.00, true, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1135, 'АНКЕТА', 0, 0, 'Normal', 12, 178, 2.00, true, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1136, '1. Фамилия $firstName
2. Имя  $name     3. Отчество $surname
4. Пол $gender   5. Дата рождения $birthYear
6. Гражданство $nationality
7. Место рождения $address
8. Образование $education
9. Какое учебное заведение закончил(а) $school', 6, 0, 'Normal', 12, 178, 3.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1137, '10. Серия, номер и дата выдачи диплома (аттестата) $schooldiplomnum
11. Специальность по диплому $speciality
12. Какой иностранный язык изучал (а) $addLanguage
13. Семейное положение $materialStatus
14. Зарегистрирован (а) по адресу $address', 6, 0, 'Normal', 12, 178, 4.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1138, '15. Адрес фактического проживания $address
16. Место работы(службы) и должность $workplace
17. Телефон(дом/раб) $phone
18. Отношение к воинской обязанности $militarydoc
19. Имели (имеете) судимость или находились (находитесь) под следствием $convictionInf', 6, 0, 'Normal', 12, 178, 5.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1139, '20. Паспорт(удостоверение, воинский билет) серия  $serialNum № $nomer
21. Наличие ПК $pcavailability
22. Владение ПК $pcskillavailability
23. Из какого источника информации узнали о нашем  институте $coordinator', 6, 0, 'Normal', 12, 178, 6.00, false, false, false);





INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1277, 'Председателю приемной комиссии, ректору АНО ВО  "Институт финансов, экономики и права офицеров запаса"
Бадмаеву С.С.
', 0, 0, 'Bold', 12, 179, 1.00, true, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1278, '                                                               от $fio --
                                                               Гражданство $nationality
                                                               Наименование документа, удостоверяющего личность                                                                                   $passtype
                                                               Серия $serialNum Номер  $nomer Кем выдан $issureName
                                                               Дата рождения $birthYear Место рождения $address
                                                               Место жительства $address
                                                               Контактный телефон $phone', 0, 0, 'Normal', 12, 179, 2.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1279, 'ЗАЯВЛЕНИЕ', 0, 0, 'Bold', 12, 179, 3.00, true, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1280, '                        Прошу принять меня на обучение в Институт финансов, экономики и права  офицеров запаса:
-по направлению подготовки $faculty,  $faccode
', 6, 0, 'Normal', 12, 179, 4.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1281, '                                                                               (Код направления, наиенование) ', 6, 0, 'Normal', 8, 179, 5.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1282, '-по профилю $speciality по форме обучения $eduType
 Имею  результаты ЕГЭ $cert.     Прошу засчитать в качестве результатов вступительных   ', 6, 0, 'Normal', 12, 179, 6.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1283, '                                                                  (да/нет)                                                                                                      ', 6, 0, 'Normal', 8, 179, 7.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1284, 'испытаний следующие предметы:   $entSubj   ', 6, 0, 'Normal', 12, 179, 8.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1285, '                                                                                             (Название предметов и  полученные баллы) ', 6, 0, 'Normal', 8, 179, 9.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1286, 'Прошу допустить к сдаче вступительных испытаний по следующим предметам ', 6, 0, 'Normal', 12, 179, 10.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1287, '
                              (Название предметов )                         ', 6, 0, 'Normal', 8, 179, 11.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1288, ' О себе сообщаю следующее: получаю высшее образование впервые $fedu                       ', 6, 0, 'Normal', 12, 179, 12.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1289, '                                                                                                                                                                                     (да/нет)    ', 6, 0, 'Normal', 8, 179, 13.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1290, 'Предыдущее  образование $school
', 6, 0, 'Normal', 12, 179, 14.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1291, '                                                                               (Полное среднее образование, СПО, ВПО)', 6, 0, 'Normal', 8, 179, 15.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1292, 'Вид документа об образовании и его реквизиты $serialNum', 6, 0, 'Normal', 12, 179, 16.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1293, '                                                                                         (Аттестат о полном среднем образовании, диплом в СПО, диплом оВПО)', 6, 0, 'Normal', 8, 179, 17.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1294, 'Необходимые условия в связи  с ограниченными возможностями здоровья или инвалидностью $socCateg
Сведения об участии в олимпиадах $award
Имею льготы ____ Документы, подтверждающие право на льготы_____
В общежитии $dorm Способ возврата документов лично
', 6, 0, 'Normal', 12, 179, 18.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1295, '                          (нуждаюсь/ нет )                                                                                 (----)                       ', 6, 0, 'Normal', 8, 179, 19.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1296, '$DataMonthYear     $fio       _____________', 6, 0, 'Normal', 12, 179, 20.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1297, '                                                    (Фамилия,-----)                                                                             (Подпись)                              ', 6, 0, 'Normal', 8, 179, 21.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1298, 'Ознакомлен:
С копией лицензии на право  осуществления образовательной деятельности с приложениями) № 0432 от 05  декабря 2012 года серия 90Л01 №0000470 _______________________________________', 6, 0, 'Normal', 12, 179, 22.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1299, '(Подпись)', 0, 0, 'Normal', 8, 179, 23.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1300, 'С копией свидетельства о государственной аккредитации (с приложениями)№0764 от 19 июля 2013 года серия 90А01 № 0000822 ) ознакомлен_________________________________________________', 6, 0, 'Normal', 12, 179, 24.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1301, '(Подпись)', 0, 0, 'Normal', 8, 179, 25.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1302, 'С правилами приема, порядком подачи апелляции по вступительным испытаниям условиями обучения, порядком оплаты образовательных услуг в ИФЭП ОЗ_______________________________', 6, 0, 'Normal', 12, 179, 26.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1303, '(Подпись)', 0, 0, 'Normal', 8, 179, 27.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1304, 'Подтверждаю одновременную подачу заявлений не более чем в пять организаций высшего образования, включая ИФЭП ОЗ__________________________________________________________', 6, 0, 'Normal', 12, 179, 28.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1339, '(Подпись)', 0, 0, 'Normal', 8, 179, 29.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1340, 'Ознакомлен с информацией о представляемых поступающим особых правил и преимуществ__________________________________________________________________________', 6, 0, 'Normal', 12, 179, 30.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1341, '(Подпись)', 0, 0, 'Normal', 8, 179, 31.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1342, 'С датой представления оригинала документа государственного орбазца обобразовании ознакомлен____________________________________________________________________________', 6, 0, 'Normal', 12, 179, 32.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1343, '(Подпись)', 0, 0, 'Normal', 8, 179, 33.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1344, 'Ознакомлен с информацией об ответственности за достоверность сведений, указанных в заявлении о приеме, и за  подлинность  документов, подаваемых для поступления ________________________', 6, 0, 'Normal', 12, 179, 34.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1345, '(Подпись)', 0, 0, 'Normal', 8, 179, 35.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1346, 'Согласен на обработку своих персональных данных                                                                    _________________                                                                                           ', 6, 0, 'Normal', 12, 179, 36.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1347, '
                                             (Подпись)', 0, 0, 'Normal', 8, 179, 37.00, false, true, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1348, 'Ответственный секретарь приемной комиссии ИФЭП ОЗ
___________          ___________            ___________ ', 6, 0, 'Normal', 12, 179, 38.00, false, false, false);
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center, rights, custom) VALUES (1349, '        (Фамилия,----)                              (Подпись)                           (Дата)', 6, 0, 'Normal', 8, 179, 39.00, false, false, false);

_________________________________________________________________CHARACTER_SET_CATALOG

ALTER TABLE student_additional_information
    ADD COLUMN school_diploma_number VARCHAR(255);

ALTER TABLE student_additional_information
  ADD COLUMN work_place VARCHAR(255);
