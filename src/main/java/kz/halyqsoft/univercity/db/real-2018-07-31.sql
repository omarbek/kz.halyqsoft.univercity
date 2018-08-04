INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.dorm.DormView', 'KK=Общага;RU=Общага;EN=Dorm;', null, 'KK=Общага;RU=Общага;EN=Dorm;',
        221, false, 'KK=Общага;RU=Общага;EN=Dorm;', true, nextval('s_tasks'), 3);

CREATE TABLE COMPLAINT
(
  ID                BIGINT PRIMARY KEY NOT NULL,
  USER_ID           BIGINT             NOT NULL,
  SHORT_DESCRIPTION VARCHAR(50)        NOT NULL,
  DESCRIPTION       VARCHAR(2048)      NOT NULL,
  CREATE_DATE       DATE               NOT NULL,
  CONSTRAINT FK_T_COMPLAINT_USER
  FOREIGN KEY (USER_ID)
  REFERENCES USERS (ID)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT
);

CREATE SEQUENCE S_COMPLAINT
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM
  DROP COLUMN ROOM_COUNT;
ALTER TABLE DORM
  DROP COLUMN BED_COUNT;

CREATE SEQUENCE S_DORM
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM_STUDENT_VIOLATION
  ADD COLUMN evicted NUMERIC(1);
UPDATE DORM_STUDENT_VIOLATION
SET evicted = 0;
ALTER TABLE DORM_STUDENT_VIOLATION
  ALTER COLUMN evicted SET NOT NULL;

ALTER TABLE dorm
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE dorm_student
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE dorm_room
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE pdf_document DROP COLUMN file_byte;
ALTER TABLE document ADD COLUMN file_byte bytea NOT NULL;

CREATE SEQUENCE S_DORM_RULE_VIOLATION_TYPE
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Несанкционированный ночлег');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Нарушение ночного режима');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Распитие спиртных напитков');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Антисанитария в жилой комнате и техпомещениях');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Нанесение материального ущерба');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Антисанитария на кухне');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Грубое отношение к работникам ДМиС');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Курение');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Нарушение правил проживания в ДМиС');

CREATE SEQUENCE S_DORM_ROOM
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_DORM_STUDENT
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_DORM_STUDENT_VIOLATION
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM_STUDENT_VIOLATION
  ALTER COLUMN evicted TYPE BOOLEAN
  USING CASE WHEN evicted = 0
  THEN FALSE
        WHEN evicted = 1
          THEN TRUE
        ELSE NULL
        END;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.finance.FinanceView', 'KK=Биллинг;RU=Биллинг;EN=Billing;', NULL,
        'KK=Биллинг;RU=Биллинг;EN=Billing;', 222, FALSE, 'KK=Биллинг;RU=Биллинг;EN=Billing;', TRUE, nextval('s_tasks'),
        3);

ALTER TABLE student_fin_debt
  ALTER COLUMN retake TYPE BOOLEAN
  USING CASE WHEN retake = 0
  THEN FALSE
        WHEN retake = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE SEQUENCE s_student_payment
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE s_student_fin_debt
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE TABLE device (
  id      BIGINT                NOT NULL,
  name    CHARACTER VARYING(64) NOT NULL
);

ALTER TABLE device
  ADD CONSTRAINT pk_device PRIMARY KEY (id);

CREATE SEQUENCE S_DEVICE
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE room
  ADD device_id BIGINT;

ALTER TABLE room
  ADD CONSTRAINT fk_room_device FOREIGN KEY (device_id)
REFERENCES device (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_room_device
  ON room (
    device_id ASC
  );

CREATE SEQUENCE S_LESSON
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE lesson
  ALTER COLUMN canceled TYPE BOOLEAN
  USING CASE WHEN canceled = 0
  THEN FALSE
        WHEN canceled = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE SEQUENCE S_LESSON_DETAIL
MINVALUE 0
START WITH 1
NO CYCLE;

alter TABLE pdf_document drop COLUMN file_byte;

--damira
INSERT INTO public.pdf_document (id, user_id, title, file_name, deleted, period) VALUES (90, 2, 'КЕЛІСІМ-ШАРТ', 'Келісім_шарт', false, 0);

INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Ақылы білім беру қызметін көрсету туралы', 0, 0, 'Normal', 12, 90, 66.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Шымкент қ.       ', 9, 9, 'Normal', 12, 90, 67.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'$DataMonthYear', 399, -18, 'Normal', 12, 90, 68.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Оңтүстік Қазақстан педагогикалық университеті мекемесі, лицензия № KZ80LAA00006618, 02.03.2016 ж. университет президенті Юнусов Мадияр Бактиярұлы университет жарғысы негізінде бір жағынан «БІЛІМ БЕРУ ҰЙЫМЫ» ретінде және «БІЛІМ АЛУШЫ»', 9, 0, 'Normal', 12, 90, 69.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'$fio,', 0, 0, 'Normal', 12, 90, 69.10, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Екінші жағынан, төмендегідей келісім-шарт жасады:', 9, 3, 'Normal', 12, 90, 71.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1. Келісім-шарт
', 0, 3, 'Bold', 12, 90, 72.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1.1 БІЛІМ БЕРУ ҰЙЫМЫ БІЛІМ АЛУШЫҒА $speciality мамандығы бойынша $formaobuch оқу формасы негізінде мемлекеттік жалпыға міндетті білім тиісті, біліктілікке диплом  жұмысын (жобасы) қорғау аяқталған соң, мемлекеттік үлгідегі жоғары кәсіби білім туралы  диплом беруге міндеттеледі.
1.2 БІЛІМ АЛУШЫ осы келісім-шарттарына сәйкес төлемақысын  төлеуді іске асыруға міндеттеледі.', 9, 3, 'Normal', 12, 90, 73.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'2. Тараптардың міндеттері мен құқықтары
', 0, 3, 'Bold', 12, 90, 74.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'2.1 БІЛІМ БЕРУ ҰЙЫМЫ міндеттеледі:
1) ҚР  Жоғары оқу орындарында қабылдаудың типтік ережелеріне сәйкес БІЛІМ АЛУШЫНЫ  осы келісім-шарттың 3.4 тармағына сәйкес оқу төлемақысының 50%-ын төлеген жағдайда студенттер қатарына қабылдауға;
2) БІЛІМ АЛУШЫНЫ  ішкі тәртіп ережелерімен таныстыруға;
3) ҚР мемлекеттік жалпыға міндетті білім беру стандартының талаптарына сәйкес маман дайындап шығаруды қамтамасыз етуге;
', 9, 3, 'Normal', 12, 90, 75.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'4) Тиісті нормативтерге сәйкес үзілістерімен   түзілген оқу жоспары бойынша білім алушының игеруі тиісті білім көлемін қамтамасыз етуге және оған қауіпсіз жағдай туғызуға;
5) БІЛІМ АЛУШЫНЫҢ оқу жоспарына сәйкес кәсіби іс-тәжірибеден өтуін ұйымдастыруға;
6) БІЛІМ АЛУШЫНЫҢ өтініші негізінде, осы мерзімдегі оның оқу жоспарын игеруі мен төлемақыны жүргізу нәтижесі бойынша оны ҚР Білім және ғылым министрлігінің ережелеріне сәйкес бір мамандықтан басқа мамандыққа немесе оқу үлгісінің бір түрінен басқа түріне, сонымен қатар басқа оқу орнына ауыстыруға;', 9, 3, 'Normal', 12, 90, 76.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'7) Өз еркімен ғылыми, мәдени және спорттық іс-шараларға, сондай-ақ халықаралық деңгейде өткізілетін іс-шараларға қатысуына мүмкіндік беруге;
8) Толық оқу курсын табысты аяқтаған жағдайда және мемлекеттік қорытынды аттестациядан өту, диплом қорғау нәтижелері бойынша БІЛІМ АЛУШЫҒА  $speciality мамандығы бойынша бакалавр дәрежесі мен мемлекеттік үлгідегі диплом беруге;
', 9, 0, 'Normal', 12, 90, 77.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'2.2. БІЛІМ БЕРУ ҰЙЫМЫ құқығы:', 9, 0, 'Bold', 12, 90, 78.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1) БІЛІМ АЛУШЫДАН осы келісім-шартқа сәйкес өзіне алған міндеттерін адал және мұқият орындауын, сондай-ақ университет оқытушыларын, қызметкерлерін және студенттерін сыйлауды, оқу тәртібін сақтауды талап етуге;
2) БІЛІМ АЛУШЫҒА оқу тәртібін, осы келісім-шарт талаптарын, ішкі тәртіпті бұзған жағдайда шара қолдануға;
', 9, 0, 'Normal', 12, 90, 79.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3) БІЛІМ АЛУШЫДАН университет мүлкіне ұқыптылықпен қарауға, компьютер және өзге техникамен жұмыс тәртібін сақтауды, егер тапсырыс беруші материалдық зиян келтірсе, ҚР заңдарында көрсетілгендей залалды орнына келтіруге талап етуге;
4) БІЛІМ АЛУШЫНЫ оқудағы, ғылыми және шығармашылық қызметтегі, қоғамдық өмірдегі қол жеткен табыстары үшін мадақтауға.
', 9, 0, 'Normal', 12, 90, 80.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'2.3 БІЛІМ АЛУШЫ міндеттенеді:
', 9, 0, 'Bold', 12, 90, 81.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1) Мемлекеттік жалпыға білім беру стандартының толық көлемінде білім мен  практикалық дағдыларды игеруді, сабақтардың барлық оқу түрлеріне қатысуға және іс-тәжірибенің барлық түрінен өтуге;
2) Университеттің  ішкі тәртібі ережелерін сақтауға;
3) Білім алғандығы үшін осы келісім-шарттың 3.4 тармағына сәйкес төлемақыны мезгілінде төлеуге;
4) Университет оқу жабдықтары мен ақпараттық ресурстарына ұқыпты қарауға және оны тиімді пайдалануға;
5) Университет оқытушылары, қызметкерлері мен студенттеріне сыйластықпен қарауға;', 9, 0, 'Normal', 12, 90, 82.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'6) Себепті жағдайлармен міндетті сабақтарға қатыса алмаған жағдайда ресми айғақты құжаттар тапсыруға;
7) Университет әкімшілігінің  талаптарына сәйкес осы келісім-шартты, ішкі тәртіпті орындау жөнінде жазбаша түсініктеме беруге;
8) Күндізгі (сырттай, кешкі) оқу түрінде БІЛІМ АЛУШЫ ағымдағы айдың 25 жұлдызына дейін, әр ай сайын жылдық төлемақы сомасының 12,5%-н төлеп отыруға міндетті;
9) Жатақхана қажет ететін БІЛІМ АЛУШЫ ағымдағы айдың 5-жұлдызына дейін 12,5%-н  төлеп отыруға міндетті.', 9, 0, 'Normal', 12, 90, 83.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'2.4 БІЛІМ АЛУШЫ құқылы:
', 9, 0, 'Bold', 12, 90, 84.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1) Оқу бағдарламасын игеру нәтижесіне сәйкес, білім беру мекемесі басшысының бұйрығы негізінде оқудың бір түрінен екіншісіне, басқа оқу орнына, басқа мамандыққа, ақылы оқудан ҚР білім және ғылым министрлігінің бекіткен мемлекеттік білім беру тапсырысына ауысуға;
2) Қосымша ақы төлеп, қосымша білім беру қызметін алуға (соның ішінде қайта емтихан тапсыру, пәнді қайта игеру, жаңа пәндерді оқу;
', 9, 0, 'Normal', 12, 90, 85.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3) Кітапхана мен оқу залында оқу, оқу-әдістемелік және ғылыми әдебиеттер қорын еркін пайдалануға;
4) Университет ғылыми кеңесінің шешіміне сәйкес осы келісімге қосымша енгізу арқылы жеке жоспар бойынша және жеделдетілген бағдарламамен оқуға;
5) Өз пікірі мен көзқарасын еркін білдіруге,
6)Адамгершілік ар-ожданына нұқсан келтірмеуді талап етуге.
', 9, 0, 'Normal', 12, 90, 86.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3. Төлем ақының көлемі мен оны төлеу тәртібі
', 0, 0, 'Bold', 12, 90, 192.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3.1 Осы келісім-шартқа сәйкес төлемақы көлемі  $money                        $InLetters .', 9, 0, 'Normal', 12, 90, 193.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'  (санмен)	                          (жазумен)
', 270, 0, 'Normal', 12, 90, 194.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'25 қыркүйекке дейін – $ansEdu теңге       25 қаңтарға дейін – $ansEdu теңге
25 қазанға дейін – $ansEdu теңге             25 ақпанға дейін – $ansEdu теңге
25 қарашаға дейін – $ansEdu теңге          25 наурызға дейін – $ansEdu теңге
25 желтоқсанға дейін – $ansEdu теңге     25 сәуірге дейін – $ansEdu теңге', 9, 0, 'Normal', 12, 90, 195.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Білім алғаны үшін төлемақы жыл басында немесе оқу жылы кезінде тәртібін анықтайтын ҚР үкіметінің Қаулысына сәйкес, ҚР Білім және ғылым министрлігінің бұйрықтарына сәйкес, ҚР үкіметінің Қаулысына сәйкес университет қызметкерлерінің жалақысы көбейген жағдайда, белгіленген тәртіпте коммуналдық қызмет тарифтерінің өсуіне және БІЛІМ БЕРУ ҰЙЫМЫНЫҢ еркінен тыс болған жағдайларға байланысты өсуі мүмкін.', 9, 0, 'Normal', 12, 90, 196.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3.2 БІЛІМ АЛУШЫ бүкіл оқу мерзімінің төлемақысын осы келісім-шарт түзілген сәтте, осы жылдың 1 қазанына дейін толық төлесе, БІЛІМ АЛУШЫҒА жылдық оқу төлемақысынан 10% жеңілдік жасалады.
3.3 БІЛІМ АЛУШЫ талап етілген мекемеге анықтама  алу үшін жылдық төлемақының  50% - ын төлеуге  міндетті', 9, 0, 'Normal', 12, 90, 197.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3.4 Оқу төлемақысын бөліп төлеген жағдайда, екі жақтың келісімі негізінде еңбек ақысының жоғарылауы мен инфляцияға байланысты төлемақының көлемі жылына 1-реттен артық өзгермеуі керек. Төлемақы көлемі өзгерген жағдайда бұл туралы осы келісім-шартқа қосымша енгізіледі. Бірақ төлемақыға жасалған өсім еңбек ақысының жоғарылауы мен жалпы төлемақыға қатысты алғандағы инфляция индексінен артпауы керек.
', 9, 0, 'Normal', 12, 90, 198.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3.5 Егер БІЛІМ АЛУШЫНЫҢ ықыласымен осы келісім-шарт бұзылатын болса, БІЛІМ БЕРУ ҰЙЫМЫ бұйрық шыққанға дейінгі мерзімге тиесілі ақшаны ұстап қалып, қалғанын қайтаруға міндетті.
3.6 Төлемақы тиісті сомманы ЖОО есеп айырысу шотына аудару арқылы жүргізіледі.

', 9, 0, 'Normal', 12, 90, 199.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'3.7 БІЛІМ АЛУШЫ академиялық демалысқа немесе оқудан шыққан жағдайда, келісім-шарт бұзылып, оқу төлемақысы демалыс аяқталған сәтте белгіленген мөлшерде жүргізіледі.
3.8 Әскери кафедрада білім алу шығындары осы келісім-шартта көрсетілген оқу ақысына кірмейді, әскери кафедрада білім алудың төлемақысы жеке келісім-шарт бойынша жүргізіледі. ', 9, 0, 'Normal', 12, 90, 199.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'4. Тараптардың жауапкершілігі
', 0, 0, 'Bold', 12, 90, 201.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'4.1 Оқу үшін төлемақы төленбеген жағдайда немесе семестр бойы себепсіз 60 сағат дәрістерге қатыспағаны үшін БІЛІМ АЛУШЫ ректордың бұйрығымен ЖОО-нан шығарылады. БІЛІМ АЛУШЫ 	4 аптаның ішінде қарызын өтеген жағдайда 40000 (қырық) мың теңге мөлшерінде айыппұл төлеп, оқуға қайта қабылданады.', 9, 0, 'Normal', 12, 90, 202.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'4.2 Егер БІЛІМ АЛУШЫ оқу ақысын төлемегені үшін оқудан шығарылса, қарызын қайтармайынша, академиялық анықтама және өзге құжаттар берілмейді. Оқуды бітірер кезде қарызы болса, оны қайтармайынша, ЖОО-ны бітіргені туралы құжат берілмейді.
4.3 БІЛІМ АЛУШЫНЫ академиялық үлгермеушілігі (GPA балын жинай алмаса) бойынша келесі жылға ақысын төлеп, оқуға қалдырылады немесе өз еркімен оқудан шығады.
4.4 Келісім-шарт мына жағдайларда бұзылады:', 9, 0, 'Normal', 12, 90, 203.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'1) БІЛІМ АЛУШЫ университеттің жарғысында көрсетілген ішкі тәртіп пен талапты бұзған жағдайда оқу үшін төленген ақы қайтарылмайды.
2) Денсаулығы, отбасы жағдайы, басқа оқу орнына ауысуы және басқа да жағдайларға байланысты БІЛІМ АЛУШЫНЫҢ өтініші бойынша оқудан шығарылады. Мұнда БІЛІМ АЛУШЫ БІЛІМ БЕРУ ҰЙЫМЫНА келтіріген шығыстарын қайтарады.
', 9, 0, 'Normal', 12, 90, 204.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'4.5 Лицензияның жойылуы немесе қайтарып алуына байланысты БІЛІМ БЕРУ ҰЙЫМЫНЫҢ – қызметі тоқталған жағдайында, БІЛІМ БЕРУ ҰЙЫМЫ осы келісім-шартта көрсетілген оқу төлемақысы шарттарын сақтай отырып, басқа білім беру ұйымдарында оқу жылын аяқтауға міндетті.
4.6 Осы келісім-шартта көрсетілмеген немесе тараптардың міндеттемелерін толық орындамаған жағдайда олар ҚР заңдарына сәйкес жауапты.', 9, 0, 'Normal', 12, 90, 205.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'5. Келісім – шарт мерзімі
', 0, 0, 'Bold', 12, 90, 206.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'5.1 Осы келісім-шарт тараптардың қол қойған сәтінен бастап өз күшіне енеді, БІЛІМ АЛУШЫНЫҢ білім алу уақыты аяқталғанға дейін және оған мемлекеттік үлгідегі кәсіби жоғары білімі туралы диплом берілгенге дейін іске асырылады.', 9, 0, 'Normal', 12, 90, 207.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'6. Келісім-шарт шарттарына өзгеріс енгізу және келісім-шартқа толықтырулыр енгізу тәртібі
', 0, 0, 'Bold', 12, 90, 208.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'6.1 Осы келісім-шарт тараптардың келісімі бойынша өзгертілуі немесе толықтырылуы мүмкін. Осы келісім-шартқа енгізілген кез келген өзгертулер мен толықтырулар, жазбаша үлгіде жүргізілгенінде ғана жарамды болып есептеледі.
', 9, 0, 'Normal', 12, 90, 209.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'7. Даулы мәселелерді шешу тәртібі
', 0, 0, 'Bold', 12, 90, 210.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'7.1 Туындаған кез-келген даулы мәселелер тараптардың келісімі бойынша немесе сот арқылы шешіледі.
', 9, 0, 'Normal', 12, 90, 211.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'8. Форс мажорлық жағдайлар
', 0, 0, 'Bold', 12, 90, 212.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'8.1 Тараптардың осы келісім-шарт түзілген соң пайда болған, алдын ала білмеген немесе саналы іс-шаралармен алдын ала болдырмауға мүмкіндік болмаған, игерілуі мүмкін емес күштер  жағдайы салдарынан, төтенше сипаттағы  оқиғалар нәтижесінде осы келісім-шарт бойынша міндеттемелерді жекелей немесе толық орындамау жауапкершілігінен босатылады. Бұл жағдайда осы келісім-шарт мерзімі әлгіндей жағдайлар  мерзіміне сай өзгертіледі.
8.2 Осы келісім-шарттың орындалуына кедергі келтіретін игерілуі мүмкін емес күштерге өрт, жер сілкінісі, су тасқыны, авариялық жағдайлар, сондай-ақ ереуілдер, әскери әрекеттер жатады.
8.3 Егер міндеттемені орындамау жағдайы 3 ай уақытқа созылса, тараптардың бірі бұл келісім-шартты бұзуға құқылы.
', 9, 0, 'Normal', 12, 90, 213.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'9. Қорытынды ереже
', 0, 0, 'Bold', 12, 90, 215.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'9.1 Осы Келісім-шарт мемлекеттік тілде  құрылған және бірдей заң күшіне ие екі нұсқада түзілген.
9.2 Егер БІЛІМ АЛУШЫ азамат кәмелеттік жасқа толмаса, онда тек ата-анасы немесе қамқоршысы ғана осы келісім-шартқа қол қоюға құқылы.', 9, 0, 'Normal', 12, 90, 216.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'10. Жақтардың заңды мекен-жайы және банк реквизиттері
', 0, 0, 'Bold', 12, 90, 217.00, true);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'ОҚПУ,  Шымкент қ., 160019, Жангелдин к-сі, 13а. тел. 53-67-82.

Банк реквизиттері:
Банк АО “KASPI BANK”
ИИК: KZ43722S000000434016
БИК: CASPKZKA
БИН: 961140000143', 9, 0, 'Normal', 10, 90, 218.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'БІЛІМ АЛУШЫНЫҢ пошталық мекен-жайы, реквизиттері:
$rekvizit, $email,  $phone.', 9, 0, 'Normal', 12, 90, 219.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'БІЛІМ БЕРУ ҰЙЫМЫ:
ОҚПУ президенті,
Юнусов М.Б.										      _________________________
	             (қолы)							     	`
', 9, 15, 'Normal', 12, 90, 221.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'БІЛІМ АЛУШЫ:
$fio
_________________________
	          (қолы)							     	`
', 306, -72, 'Normal', 12, 90, 222.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'Келісім-шарт талаптары мен Ішкі еңбек тәртібі ережелерімен таныстым
                                                                                _________________________________
                                                                                     ', 243, 21, 'Normal', 12, 90, 223.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'БІЛІМ АЛУШЫНЫҢ қолы                                                                             ', 417, -18, 'Normal', 8, 90, 224.00, false);
INSERT INTO public.pdf_property (id,text, x, y, font, size, pdf_document_id, order_number, center) VALUES (nextval('s_pdf_property'),'$data$month$year', 399, 0, 'Normal', 12, 90, 225.00, false);

-- CATALOG
CREATE TABLE CATALOG (
  id BIGINT NOT NULL ,
  name VARCHAR(255) NOT NULL UNIQUE ,
  value text NOT NULL,
  description text NOT NULL ,
  created TIMESTAMP NOT NULL
);

create sequence S_CATALOG
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE groups ADD COLUMN curator_id BIGINT NULL ;
ALTER TABLE groups ADD CONSTRAINT fk_groups_employee FOREIGN KEY (curator_id) REFERENCES employee(id);