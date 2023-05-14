INSERT INTO public.courier (courier_id, courier_type)
VALUES (1, 'AUTO');
INSERT INTO public.courier (courier_id, courier_type)
VALUES (2, 'BIKE');
INSERT INTO public.courier (courier_id, courier_type)
VALUES (3, 'FOOT');
INSERT INTO public.courier (courier_id, courier_type)
VALUES (4, 'FOOT');
INSERT INTO public.courier (courier_id, courier_type)
VALUES (5, 'AUTO');


INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (1, 1);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (1, 2);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (1, 3);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (2, 2);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (2, 4);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (3, 4);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (4, 1);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (5, 2);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (5, 3);
INSERT INTO public.courier_regions (courier_courier_id, regions)
VALUES (5, 5);

INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (1, '15:30:00', '08:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (1, '21:00:00', '18:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (2, '12:30:00', '09:30:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (2, '16:25:00', '14:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (2, '23:00:00', '20:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (3, '17:00:00', '08:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (4, '12:30:00', '06:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (4, '16:25:00', '14:00:00');
INSERT INTO public.courier_working_hours (courier_courier_id, "end", start)
VALUES (5, '23:00:00', '20:00:00');