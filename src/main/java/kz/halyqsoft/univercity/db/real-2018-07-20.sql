SELECT setval('s_pdf_property', (SELECT MAX(id)
                                 FROM pdf_property) + 1);--406

INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.5 В случае расторжения настоящего Договора по инициативе ОБУЧАЮЩЕГОСЯ (по собственному желанию) ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ обязана вернуть переплаченные деньги за будущие периоды обучения с учетом вычета расходов за текущий период обучения в момент издания приказа.
3.6  Оплата производится путем внесения соответствующей суммы на расчетный счет вуза.
', 9, 0, 'Normal', 12, 92, 199.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.7 В случае отчисления или предоставления академического отпуска, расторжения с ОБУЧАЮЩИМСЯ настоящего договора, оплата за обучение производится в размере и на условиях, действующих на момент прекращения отпуска или восстановления.
3.8 Расходы  по обучению на военной кафедре не  входят в стоимость    обучения  по настоящему договору. За обучение на военной кафедре оплата производится самостоятельно.
', 9, 0, 'Normal', 12, 92, 200.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '4.Ответственность сторон', 0, 0, 'Bold', 12, 92, 201.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '4.1 В случае неоплаты за обучение и (или) пропуска более 60 часов занятий за семестр без уважительной причины ОБУЧАЮЩИЙСЯ отчисляется из вуза приказом ректора. Погашение ОБУЧАЮЩИМСЯ задолженности дает ему право на восстановление в течении четырех недель с момента отчисления, размер суммы штрафа за восстановление составляет  40 000 (сорок) тысяч тенге.', 9, 0, 'Normal', 12, 92, 202.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '9. Заключительные положения', 0, 0, 'Bold', 12, 92, 215.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'ОБУЧАЮЩИЙСЯ:
$fio
______________________________
                      (подпись)
', 306, -72, 'Normal', 12, 92, 222.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'оказания платных образовательных услуг', 0, 0, 'Normal', 12, 92, 66.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'именуемый  в дальнейшем  «ОБУЧАЮЩИЙСЯ»,- с другой  стороны, заключили  настоящий договор о нижеследующем:', 9, 3, 'Normal', 12, 92, 71.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'I. Предмет договора', 0, 3, 'Bold', 12, 92, 72.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '$data$month$year', 315, 0, 'Normal', 12, 92, 224.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '1.1 ОРГАНИЗАЦИЯ   ОБРАЗОВАНИЯ    принимает   на   себя    обязательства   по   обучению ОБУЧАЮЩЕГОСЯ по специальности $faculty в   соответствии    с   государственными    общеобязательными    стандартами    образования    по $formaobuch форме обучения с присвоением ему по окончании полного курса обучения, сдачи государственных экзаменов и(или) защиты дипломной работы (проектов) соответствующей квалификации с выдачей диплома о высшем образовании государственного образца.
1.2 ОБУЧАЮЩИЙСЯ обязуется произвести оплату за обучение в соответствии с условиями настоящего договора.
', 9, 3, 'Normal', 12, 92, 73.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '2. Обязанности и права сторон', 0, 3, 'Bold', 12, 92, 74.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '4) определить объем учебной нагрузки и режим занятий обучающегося с перерывами в соответствии с существующими нормативами, создать здоровые, безопасные условия обучения обучающегося;
5) организовать прохождение профессиональной практики ОБУЧАЮЩЕГОСЯ в соответствии с учебным планом;
6) переводить обучающегося на основании заявления с одной специальности на другую или с одной формы обучения на другую, а также в другую организацию образования в порядке, установленным Министерством образования и науки РК. исходя из результатов прохождения учебной программы и оплаты, произведенной на момент перевода;
', 9, 0, 'Normal', 12, 92, 76.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '1) овладевать знаниями, умениями и практическими навыками в полном объеме государственных общеобязательных стандартов образования, посещать все виды учебных занятий и проходить все виды практик;
2) соблюдать требования Правил внутреннего распорядка и иных актов руководителя университета;
3) своевременно производить оплату за обучение в вузе в соответствии с п 3.4. настоящего договора.
4) бережно относиться к имуществу университета и рационально использовать его;
5) уважительно и корректно относиться к преподавателям, сотрудникам и студентам вуза;
', 9, 0, 'Normal', 12, 92, 82.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'г.Шымкент', 9, 9, 'Normal', 12, 92, 67.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '4.2 В случае отчисления ОБУЧАЮЩЕГОСЯ по причине неоплаты за обучение, академическая справка и иные документы не выдаются до погашения задолженности. При образовании задолженности по оплате к моменту окончания учебы документ об окончании не выдается.
4.3 В случае академической неуспеваемости  ОБУЧАЮЩИЙСЯ остается на повторный
год обучения с внесением оплаты или же имеет право на отчисление по собственному желанию.
4.4	Договор может быть расторгнут:
', 9, 0, 'Normal', 12, 92, 203.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '1)	при отчислении ОБУЧАЮЩЕГОСЯ за нарушение Правил внутреннего распорядка и требований, указанных в Уставе вуза. При этом сумма, оплаченная за обучение, возврату не подлежит;
2)	по заявлению ОБУЧАЮЩЕГОСЯ в связи с состоянием здоровья, семейными обстоятельствами, переводом в другую организацию образования и по другим основаниям. При этом ОБУЧАЮЩИЙСЯ оплачивает ОРГАНИЗАЦИИ ОБРАЗОЗАНИЯ фактически понесенные им расходы.
', 9, 0, 'Normal', 12, 92, 204.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '4.5 В случае прекращения деятельности ОРГАНИЗАЦИИ ОБРАЗОВАНИЯ в связи с ликвидацией или отзывом лицензии. ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ обязана принять меры для завершения учебного года ОБУЧАЮЩЕГОСЯ в других организациях образования с сохранением условий оплаты за обучение, предусмотренных настоящим договором.
4.6 За неисполнение, либо ненадлежащее исполнение Сторонами своих обязанностей, в
случаях, не предусмотренных настоящим Договором, они несут ответственность в соответствии с действующим законодательством РК.
', 9, 0, 'Normal', 12, 92, 205.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '5. Срок действия Договора', 0, 0, 'Bold', 12, 92, 206.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '5.1  Настоящий договор вступает в силу с момента его подписания Сторонами и действует до завершения обучения ОБУЧАЮЩЕГОСЯ и выдачи ему диплома о высшем образовании государственного образца.', 9, 0, 'Normal', 12, 92, 207.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '6. Порядок изменений условий договора и принятия дополнений к Договору', 0, 0, 'Bold', 12, 92, 208.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '2.1 ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ обязуется:
1)	в соответствии с Типовыми правилами приема в высшие учебные заведения РК зачислить ОБУЧАЮЩЕГОСЯ  в число студентов при условии внесения им 50%  годовой оплаты за обучение в соответствии с пунктом 3.4. настоящего договора;
2)  ознакомить ОБУЧАЮЩЕГОСЯ с  Правилами внутреннего распорядка;
3) обеспечить подготовку специалиста в соответствии с требованиями государственного общеобязательного стандарта РК;', 9, 3, 'Normal', 12, 92, 75.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.1.1 Оплата за проживание в общежитии составляет:   $Obshaga            $Dorm ', 9, 0, 'Normal', 12, 92, 195.10, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '      (цифрами)              (прописью)', 276, 0, 'Normal', 12, 92, 195.20, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'до 5 сентября – 7000 тенге                      до 5 февраля – 7000 тенге
до 5 октября – 7000 тенге                       до 5 марта – 7000 тенге
до 5 ноября  – 7000 тенге                       до 5 апреля –  7000 тенге
до 5 декабря – 7000 тенге                       до 5 мая – 7000 тенге
до 5 января – 7000 тенге
', 9, 0, 'Normal', 12, 92, 195.30, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '6.1 Настоящий Договор может быть изменен или дополнен по соглашению сторон. Любые изменения и дополнения к настоящему договору действительны лишь при условии, если они совершены в письменной форме.', 9, 0, 'Normal', 12, 92, 209.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '7.Порядок разрешения споров', 0, 0, 'Bold', 12, 92, 210.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '7.1 Все споры, разрешаются соглашением Сторон или в судебном порядке.', 9, 0, 'Normal', 12, 92, 211.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '8.Форс-мажорные обстоятельства', 0, 0, 'Bold', 12, 92, 212.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '8.1 Стороны освобождаются от ответственности за частичное или полное неисполнение обязательств по настоящему Договору, если оно явилось следствием обстоятельств непреодолимой силы, возникших после заключения Договора, в результате событий чрезвычайного характера, которые Стороны не смогли предвидеть или предотвратить. При этом срок исполнения обязательств по настоящему договору отодвигается соразмерно времени, в течение которого действовали такие обстоятельства.', 9, 0, 'Normal', 12, 92, 213.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '8.2 К обстоятельствам непреодолимой силы относятся пожар, землетрясение, наводнение, аварийные ситуации, а также забастовки, военные действия, препятствующие выполнению настоящего Договора.
8.3 Если невыполнение обязательств длится более 3-х месяцев, то каждая из Сторон вправе расторгнуть настоящий договор в одностороннем порядке.
', 9, 0, 'Normal', 12, 92, 214.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '9.1 Настоящий Договор составлен на русском языке в количестве двух экземпляров, имеющих равную юридическую силу.
9.2 В случае, если ОБУЧАЮЩИЙСЯ не достиг совершеннолетнего возраста Договор подписывается родителями или лицами их заменяющими в соответствии с законодательством РК.
', 9, 0, 'Normal', 12, 92, 216.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '10. Юридические адреса и банковские реквизиты Сторон:', 0, 0, 'Bold', 12, 92, 217.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'ЮКПУ, г.Шымкент, 160019,  ул.Джангильдина, д.13. Тел. 53-67-82
Банковские реквизиты:
Банк  филиал АО “KASPI BANK” г.Шымкент
Р/счет KZ43722S000000434016
БИК: CASPKZKA
БИН: 961140000143
', 9, 0, 'Normal', 12, 92, 218.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'Почтовый адрес, телефон, реквизиты "ОБУЧАЮЩЕГОСЯ" $rekvizit,  $email, $phone', 9, 0, 'Normal', 12, 92, 220.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ:
Президент ЮКПУ М.Б.Юнусов
_____________________________
                  (подпись)
', 9, 15, 'Normal', 12, 92, 221.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'С Условиями договора и Правилами внутреннего                           распорядка ознакомлен
ОБУЧАЮЩИЙСЯ: _______________________                                                                                               ', 243, 21, 'Normal', 12, 92, 223.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '(подпись)', 381, -18, 'Normal', 10, 92, 223.10, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '$DataMonthYear', 420, -21, 'Normal', 12, 92, 68.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'Учреждение Южно-Казахстанский педагогический университет, лицензия № KZ80LAA00006618 от 02.03.2016 г., в лице президента Юнусова Мадияра Бахтияровича, действующего на основании Устава, именуемый в дальнейшем «ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ»,	с	одной	стороны,	и гр.,', 9, 0, 'Normal', 12, 92, 69.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '7) предоставлять возможность на добровольной основе принимать участие в научных, культурных и спортивных мероприятиях, а также в международной, научной, культурной и спортивной жизни;
8) после успешного окончания полного курса обучения и по результатам прохождения итоговой государственной аттестации присвоить ОБУЧАЮЩЕМУСЯ степень бакалавра по специальности $faculty и выдать диплом государственного образца.', 9, 0, 'Normal', 12, 92, 77.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '2.2 ОРГАНИЗАЦИЯ ОБРАЗОВАНИЯ  имеет право:', 9, 0, 'Bold', 12, 92, 78.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '1) требовать от ОБУЧАЮЩЕГОСЯ добросовестного и надлежащего исполнения обязанностей в соответствии с настоящим Договором и Правилами внутреннего распорядка университета, а также соблюдения учебной дисциплины, корректного и уважительного отношения к преподавателям, сотрудникам и студентам  университета;
2) применять к ОБУЧАЮЩЕМУСЯ меры дисциплинарного воздействия за нарушения им учебной дисциплины, условий настоящего Договора, Правил внутреннего распорядка;
', 9, 0, 'Normal', 12, 92, 79.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '(цифрами)              (прописью)', 270, 0, 'Normal', 12, 92, 194.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3) требовать от ОБУЧАЮЩЕГОСЯ бережного отношения к имуществу университета, соблюдения правил работы с компьютерной техникой. В случае причинения материального ущерба действиями ОБУЧАЮЩЕГОСЯ требовать возмещения внесенных затрат на его восстановление в порядке, предусмотренном действующим законодательством РК;
4) осуществлять поощрение и вознаграждение ОБУЧАЮЩЕГОСЯ за успехи в учебной, научной и творческой деятельности,
', 9, 0, 'Normal', 12, 92, 80.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '2.3 Обязанности ОБУЧАЮЩЕГОСЯ:', 9, 0, 'Bold', 12, 92, 81.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '6) в случае пропуска занятий по уважительным причинам предоставить документы, подтверждающие пропуск;
7) по требованию администрации вуза предоставить письменное объяснение по вопросам соблюдения условий настоящего Договора. Правил внутреннего распорядка и иных нормативных документов вуза.
8) ОБУЧАЮЩИЙСЯ на дневной (заочной, вечерней) форме обучения обязан ежемесячно производить оплату в размере 12,5%  от годовой суммы оплаты за обучение до 25 числа текущего месяца.
9) ОБУЧАЮЩИЙСЯ, нуждающий в общежитии  обязан ежемесячно производить оплату в размере 12,5%  до 5 числа текущего месяца.
', 9, 0, 'Normal', 12, 92, 83.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '2.4 ОБУЧАЮЩИЙСЯ имеет право на:', 9, 0, 'Bold', 12, 92, 84.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '$fio,', 0, 0, 'Normal', 12, 92, 69.10, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '1) перевод с одной формы обучения на другую, с одной организации образования в другую, с одной специальности на другую, а также перевода с платного на обучение по государственному образовательному заказу в установленном Министерством образования и науки РК, исходя из результатов освоения учебной программы;
2) получение дополнительных образовательных услуг за дополнительную оплату (включая пересдачу экзаменов, повторное изучение дисциплин, изучение новых дисциплин;
', 9, 0, 'Normal', 12, 92, 86.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3) свободный доступ и пользование фондом учебной, учебно-методической и научной литературы на базе библиотеки и читальных залов;
4) обучение по индивидуальным планам и ускоренным программам по решению Ученого совета вуза, с закреплением вышеназванных возможностей дополнительным приложением к настоящему Договору;
5) свободное выражение собственных мнений и убеждений;
6) уважение своего человеческого достоинства.
', 9, 0, 'Normal', 12, 92, 87.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.1 Оплата по настоящему договору составляет:    $money                 $InLetters', 9, 0, 'Normal', 12, 92, 193.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3. Размер и порядок оплаты', 0, 0, 'Bold', 12, 92, 192.00, 'true');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'до 25 сентября – 21250 тенге                     до 25 января – 21250 тенге
до 25 октября – 21250 тенге                      до 25 февраля – 21250 тенге
до 25 ноября – 21250 тенге                       до 25 марта – 21250 тенге
до 25 декабря – 21250 тенге                      до 25 апреля – 21250 тенге
', 9, 0, 'Normal', 12, 92, 195.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), 'Оплата за обучение может быть изменена в начале и в течение учебного года в соответствии с Постановлениями Правительства Республики Казахстан, определяющими порядок оплаты за обучение, приказами Министерства образования и науки РК, повышением заработной платы работникам университета в соответствии с постановлением Правительства РК, повышением тарифов на коммунальные услуги, утвержденных в установленном порядке, и другими  обстоятельствами, не зависящими от ОРГАНИЗАЦИИ ОБРАЗОВАНИЯ.', 9, 0, 'Normal', 12, 92, 196.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.2 В случае оплаты студентом расходов за весь год обучения единовременно договора до 1 октября текущего года, ОБУЧАЮЩЕМУСЯ предоставляется 10% скидка от годовой стоимости обучения.
3.3 ОБУЧАЮЩИЙСЯ, для получения утвержденных форм справок по месту требования, обязан внести 50% от годовой суммы оплаты.
', 9, 0, 'Normal', 12, 92, 197.00, 'false');
INSERT INTO public.pdf_property (id, text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'), '3.4 При поэтапной оплате расходов за обучение, размер оплаты может быть изменен не более 1 раза в год по соглашению сторон в случае увеличения расходов на оплату труда и индекса инфляции, об этом составляется дополнительное соглашение к настоящему договору. Рост стоимости обучения не может превышать, роста расходов на оплату труда и индекса инфляции в отношении к общему объему расходов на обучение.', 9, 0, 'Normal', 12, 92, 198.00, 'false');

UPDATE pdf_document
SET deleted = FALSE
WHERE id = 92;

drop view v_load_to_chair_count_all;
drop view v_load_to_chair_count;
drop view v_load_to_chair;
drop view v_curriculum_subject;
drop view v_curriculum_detail;
drop view v_elective_subject;
drop view v_curriculum_add_program;
drop view v_curriculum_after_semester;

ALTER TABLE curriculum_detail
  ALTER COLUMN code TYPE CHARACTER VARYING(100);
ALTER TABLE elective_subject
  ALTER COLUMN code TYPE CHARACTER VARYING(100);
ALTER TABLE curriculum_add_program
  ALTER COLUMN code TYPE CHARACTER VARYING(100);
ALTER TABLE curriculum_after_semester
  ALTER COLUMN code TYPE CHARACTER VARYING(100);

CREATE OR REPLACE VIEW V_CURRICULUM_AFTER_SEMESTER AS
  SELECT
    curr_after_sem.ID,
    curr_after_sem.CURRICULUM_ID,
    curr_after_sem.SUBJECT_ID,
    subj.NAME_RU           SUBJECT_NAME_RU,
    curr_after_sem.CODE    SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    curr_after_sem.SEMESTER_ID,
    sem.SEMESTER_NAME,
    edu_mod_type.id        education_module_type_id,
    edu_mod_type.type_name education_module_type_name,
    curr_after_sem.DELETED
  FROM CURRICULUM_AFTER_SEMESTER curr_after_sem INNER JOIN SUBJECT subj ON curr_after_sem.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN semester sem ON curr_after_sem.semester_id = sem.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = curr_after_sem.education_module_type_id;

CREATE OR REPLACE VIEW V_CURRICULUM_ADD_PROGRAM AS
  SELECT
    curr_add_pr.ID,
    curr_add_pr.CURRICULUM_ID,
    curr_add_pr.SUBJECT_ID,
    subj.NAME_RU SUBJECT_NAME_RU,
    curr_add_pr.CODE    SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    curr_add_pr.SEMESTER_ID,
    sem.SEMESTER_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    curr_add_pr.DELETED
  FROM CURRICULUM_ADD_PROGRAM curr_add_pr INNER JOIN SUBJECT subj ON curr_add_pr.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN SEMESTER sem ON curr_add_pr.SEMESTER_ID = sem.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = curr_add_pr.education_module_type_id;

CREATE OR REPLACE VIEW V_ELECTIVE_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    d.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.subject_cycle_id = 4
      THEN a.subject_cycle_id
    ELSE c.subject_cycle_id END SUBJECT_CYCLE_ID,
    CASE WHEN c.subject_cycle_id = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE g.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    f.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    e.FORMULA,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    a.consider_credit,
    a.DELETED
  FROM ELECTIVE_SUBJECT a INNER JOIN CURRICULUM b ON b.ID = a.CURRICULUM_ID
    INNER JOIN SUBJECT c ON c.ID = a.SUBJECT_ID
    INNER JOIN SEMESTER d ON d.ID = a.SEMESTER_ID
    INNER JOIN ACADEMIC_FORMULA e ON c.ACADEMIC_FORMULA_ID = e.ID
    INNER JOIN CREDITABILITY f ON c.CREDITABILITY_ID = f.ID
    INNER JOIN SUBJECT_CYCLE g ON c.SUBJECT_CYCLE_ID = g.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = a.education_module_type_id
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID;

CREATE OR REPLACE VIEW V_CURRICULUM_DETAIL AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN a.SUBJECT_CYCLE_ID
    ELSE c.SUBJECT_CYCLE_ID END SUBJECT_CYCLE_ID,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE d.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    e.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    f.FORMULA,
    a.RECOMMENDED_SEMESTER,
    a.CONSIDER_CREDIT,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    a.DELETED,
    FALSE                       ELECTIVE
  FROM CURRICULUM_DETAIL a INNER JOIN SEMESTER b ON a.SEMESTER_ID = b.ID
    INNER JOIN SUBJECT c ON a.SUBJECT_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON c.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON c.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON c.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = a.education_module_type_id
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID;

CREATE OR REPLACE VIEW V_CURRICULUM_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN a.SUBJECT_CYCLE_ID
    ELSE c.SUBJECT_CYCLE_ID END SUBJECT_CYCLE_ID,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE d.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    e.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    f.FORMULA,
    a.RECOMMENDED_SEMESTER,
    a.CONSIDER_CREDIT,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    a.DELETED,
    FALSE                       ELECTIVE
  FROM CURRICULUM_DETAIL a INNER JOIN SEMESTER b ON a.SEMESTER_ID = b.ID
    INNER JOIN SUBJECT c ON a.SUBJECT_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON c.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON c.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON c.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID
  UNION ALL
  SELECT
    aa.ID,
    aa.CURRICULUM_ID,
    aa.SEMESTER_ID,
    bb.SEMESTER_NAME,
    aa.subject_id                SUBJECT_ID,
    aa.code                      SUBJECT_CODE,
    subject.NAME_RU              SUBJECT_NAME,
    CASE WHEN aa.subject_cycle_id IS NULL
      THEN cycle.id
    ELSE aa.subject_cycle_id END SUBJECT_CYCLE_ID,
    CASE WHEN dd.CYCLE_SHORT_NAME
              IS NULL
      THEN cycle.cycle_short_name
    ELSE dd.cycle_short_name END,
    credit.id                    CREDITABILITY_ID,
    credit.credit                CREDIT,
    subject.academic_formula_id  ACADEMIC_FORMULA_ID,
    f.formula                    FORMULA,
    NULL                         RECOMMENDED_SEMESTER,
    aa.CONSIDER_CREDIT,
    subject.control_type_id      CONTROL_TYPE_ID,
    h.type_name                  CONTROL_TYPE_NAME,
    aa.DELETED,
    TRUE                         ELECTIVE
  FROM elective_subject aa INNER JOIN SEMESTER bb ON aa.SEMESTER_ID = bb.ID
    LEFT JOIN SUBJECT_CYCLE dd ON aa.subject_cycle_id = dd.ID
    INNER JOIN subject subject ON subject.id = aa.subject_id
    INNER JOIN creditability credit ON credit.id = subject.creditability_id
    INNER JOIN subject_cycle cycle ON cycle.id = subject.subject_cycle_id
    INNER JOIN ACADEMIC_FORMULA f ON subject.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON subject.CONTROL_TYPE_ID = h.ID;

CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_subj.subject_name,
    CASE WHEN curr_subj.semester_id IN (1, 2)
      THEN 1
    WHEN curr_subj.semester_id IN (3, 4)
      THEN 2
    WHEN curr_subj.semester_id IN (5, 6)
      THEN 3
    WHEN curr_subj.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_subj.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                                  total_count
  FROM v_curriculum_subject curr_subj
    INNER JOIN subject subj ON subj.id = curr_subj.subject_id
    INNER JOIN curriculum curr ON curr_subj.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_subj.semester_id
  WHERE curr_subj.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_subj.semester_id, sem.semester_name, curr_subj.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_subj.id, subj.id, curr.id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_add_pr.subject_name_ru                   subject_name,
    CASE WHEN curr_add_pr.semester_id IN (1, 2)
      THEN 1
    WHEN curr_add_pr.semester_id IN (3, 4)
      THEN 2
    WHEN curr_add_pr.semester_id IN (5, 6)
      THEN 3
    WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_add_pr.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                                  total_count
  FROM v_curriculum_add_program curr_add_pr
    INNER JOIN subject subj ON subj.id = curr_add_pr.subject_id
    INNER JOIN curriculum curr ON curr_add_pr.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
  WHERE curr_add_pr.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_add_pr.semester_id, sem.semester_name, curr_add_pr.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_add_pr.id, subj.id, curr.id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_after_sem.subject_name_ru                subject_name,
    CASE WHEN curr_after_sem.semester_id IN (1, 2)
      THEN 1
    WHEN curr_after_sem.semester_id IN (3, 4)
      THEN 2
    WHEN curr_after_sem.semester_id IN (5, 6)
      THEN 3
    WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_after_sem.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                                  total_count
  FROM v_curriculum_after_semester curr_after_sem
    INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id
    INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_after_sem.semester_id
  WHERE curr_after_sem.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_after_sem.semester_id, sem.semester_name, curr_after_sem.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_after_sem.id, subj.id, curr.id
  ORDER BY semester_name;

CREATE OR REPLACE VIEW v_load_to_chair_count AS
  SELECT
    study_year,
    curriculum_id,
    subj.chair_id,
    sum(load.lc_count)           lc_count,
    sum(load.pr_count)           pr_count,
    sum(load.lb_count)           lb_count,
    sum(load.with_teacher_count) with_teacher_count,
    sum(rating_count)            rating_count,
    sum(exam_count)              exam_count,
    sum(control_count)           control_count,
    sum(course_work_count)       course_work_count,
    sum(diploma_count)           diploma_count,
    sum(practice_count)          practice_count,
    sum(mek)                     mek,
    sum(protect_diploma_count)   protect_diploma_count,
    sum(load.total_count)        total_count
  FROM v_load_to_chair load INNER JOIN subject subj ON subj.id = load.subject_id
  GROUP BY study_year, curriculum_id,subj.chair_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
  SELECT
    curriculum_id,
    chair_id,
    sum(lc_count)              lc_count,
    sum(pr_count)              pr_count,
    sum(lb_count)              lb_count,
    sum(with_teacher_count)    with_teacher_count,
    sum(rating_count)          rating_count,
    sum(exam_count)            exam_count,
    sum(control_count)         control_count,
    sum(course_work_count)     course_work_count,
    sum(diploma_count)         diploma_count,
    sum(practice_count)        practice_count,
    sum(mek)                   mek,
    sum(protect_diploma_count) protect_diploma_count,
    sum(total_count)           total_count
  FROM v_load_to_chair_count
  GROUP BY curriculum_id, chair_id;