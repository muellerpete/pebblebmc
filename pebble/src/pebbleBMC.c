#include "pebble_os.h"
#include "pebble_app.h"
#include "pebble_fonts.h"
#include "pebblebmc_constants.h"

#define MY_UUID { 0xEE, 0xE6, 0x57, 0x60, 0x8E, 0xA9, 0x41, 0xD1, 0xAC, 0x84, 0xDE, 0x8D, 0xE9, 0x15, 0x95, 0xAA }
PBL_APP_INFO(MY_UUID,
             "pebbleBMC", "Muellerpete",
             1, 0, /* App version */
             DEFAULT_MENU_ICON,
             APP_INFO_STANDARD_APP);

Window window;
TextLayer textLayer;
TextLayer textLayer2;
Window playerWindow;
TextLayer playerTextLayerRow1;
TextLayer playerTextLayerRow2;
TextLayer playerTextLayerRow3;
TextLayer playerTextLayerRow4;
TextLayer playerTextLayer5;
AppSync sync;
uint8_t sync_buffer[128];

enum {
	LONG_CLICK_UP = FALSE,
	LONG_CLICK_DOWN = FALSE,
	LONG_CLICK_SELECT = FALSE,
	LONG_CLICK_BACK = FALSE,
};

/**
 *   Handler for AppMessage sent
 */
void out_sent_handler(DictionaryIterator *sent, void *context) {
    text_layer_set_text(&textLayer, "Send successful!");
}

static int playerOnTop() {
	return (window_stack_get_top_window() == &playerWindow);
}
/**
 *   Handler for received AppMessage
 */
static void sync_tuple_changed_callback(const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context) {

	switch (key) {
	case ON_SHOW_PLAYER:
		if (new_tuple->value->uint8) {
			if (!playerOnTop()) {
				window_stack_push(&playerWindow, TRUE);
			}
		} else {
			if (playerOnTop()) {
				window_stack_pop(TRUE);
			}
    	}
    	break;
	case ON_ROW_1:
		text_layer_set_text(&playerTextLayerRow1, new_tuple->value->cstring);
		break;
	case ON_ROW_2:
		text_layer_set_text(&playerTextLayerRow2, new_tuple->value->cstring);
		break;
	case ON_ROW_3:
		text_layer_set_text(&playerTextLayerRow3, new_tuple->value->cstring);
		break;
	case ON_ROW_4:
		text_layer_set_text(&playerTextLayerRow4, new_tuple->value->cstring);
		break;
	default:
	    return;
	}
}

/**
 *   Function to send a key press using a pre-agreed key
 */
static void send_button_click(uint8_t button, uint8_t isLong, uint8_t numClicks) {
    DictionaryIterator *iter;
    app_message_out_get(&iter);
    if (iter == NULL)
        return;
    Tuplet actionTuplet = TupletInteger(BUTTON_CLICK, button);
    uint8_t topWindow = NAVIGATOR_WINDOW;
    if (playerOnTop()) {
    	topWindow = PLAYER_WINDOW;
    }
    Tuplet windowTuplet = TupletInteger(WINDOW, topWindow);
    Tuplet isLongTuplet = TupletInteger(BUTTON_LONG, isLong);
    Tuplet buttonNumClicksTuplet = TupletInteger(BUTTON_NUM_CLICKS, numClicks);

    dict_write_tuplet(iter, &actionTuplet);
    dict_write_tuplet(iter, &windowTuplet);
    dict_write_tuplet(iter, &isLongTuplet);
    dict_write_tuplet(iter, &buttonNumClicksTuplet);
    dict_write_end(iter);

    app_message_out_send();
    app_message_out_release();
}

void up_click_handler(ClickRecognizerRef recognizer, Window *window) {
    const uint16_t count = click_number_of_clicks_counted(recognizer);
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_UP, FALSE, count);
}
void up_long_click_handler(ClickRecognizerRef recognizer, Window *window) {
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_UP, TRUE, 1);
}

void down_click_handler(ClickRecognizerRef recognizer, Window *window) {
        const uint16_t count = click_number_of_clicks_counted(recognizer);
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_DOWN, 0, count);
}
void down_long_click_handler(ClickRecognizerRef recognizer, Window *window) {
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_DOWN, TRUE, 1);
}

void select_click_handler(ClickRecognizerRef recognizer, Window *window) {
        const uint8_t count = click_number_of_clicks_counted(recognizer);
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_SELECT, 0, count);
}
void select_long_click_handler(ClickRecognizerRef recognizer, Window *window) {
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_SELECT, TRUE, 1);
}

void back_click_handler(ClickRecognizerRef recognizer, Window *window) {
        const uint8_t count = click_number_of_clicks_counted(recognizer);
    (void)recognizer;
    (void)window;
    if (count == 2) {
    	window_stack_pop(TRUE);
    } else {
    	send_button_click(BUTTON_ID_SELECT, 0, count);
    }
}
void back_long_click_handler(ClickRecognizerRef recognizer, Window *window) {
    (void)recognizer;
    (void)window;
    send_button_click(BUTTON_ID_BACK, TRUE, 1);
}

void click_config_provider(ClickConfig **config, Window *window) {
    (void)window;

    config[BUTTON_ID_UP]->click.handler = (ClickHandler) up_click_handler;
    config[BUTTON_ID_UP]->multi_click.handler = (ClickHandler) up_click_handler;
    config[BUTTON_ID_UP]->multi_click.min = 2;
    config[BUTTON_ID_UP]->multi_click.max = 3;
    config[BUTTON_ID_UP]->multi_click.last_click_only = true;
    config[BUTTON_ID_UP]->multi_click.timeout = 250;
    if (LONG_CLICK_UP) {
    	config[BUTTON_ID_UP]->long_click.delay_ms = 400;
    	config[BUTTON_ID_UP]->long_click.handler = (ClickHandler) up_long_click_handler;
    } else {
    	config[BUTTON_ID_UP]->click.repeat_interval_ms = 400;
    }

    config[BUTTON_ID_DOWN]->click.handler = (ClickHandler) down_click_handler;
    config[BUTTON_ID_DOWN]->multi_click.handler = (ClickHandler) down_click_handler;
    config[BUTTON_ID_DOWN]->multi_click.min = 2;
    config[BUTTON_ID_DOWN]->multi_click.max = 3;
    config[BUTTON_ID_DOWN]->multi_click.last_click_only = true;
    if (LONG_CLICK_DOWN) {
    	config[BUTTON_ID_DOWN]->long_click.delay_ms = 400;
    	config[BUTTON_ID_DOWN]->long_click.handler = (ClickHandler) down_long_click_handler;
    } else {
    	config[BUTTON_ID_DOWN]->click.repeat_interval_ms = 400;
    }

    config[BUTTON_ID_SELECT]->click.handler = (ClickHandler) select_click_handler;
    config[BUTTON_ID_SELECT]->multi_click.handler = (ClickHandler) select_click_handler;
    config[BUTTON_ID_SELECT]->multi_click.min = 2;
    config[BUTTON_ID_SELECT]->multi_click.max = 3;
    config[BUTTON_ID_SELECT]->multi_click.last_click_only = true;
    if (LONG_CLICK_SELECT) {
    	config[BUTTON_ID_SELECT]->long_click.delay_ms = 400;
    	config[BUTTON_ID_SELECT]->long_click.handler = (ClickHandler) select_long_click_handler;
    } else {
        config[BUTTON_ID_SELECT]->click.repeat_interval_ms = 400;
    }

    config[BUTTON_ID_BACK]->click.handler = ( ClickHandler ) back_click_handler;
    config[BUTTON_ID_BACK]->multi_click.handler = (ClickHandler) back_click_handler;
    config[BUTTON_ID_BACK]->multi_click.min = 2;
    config[BUTTON_ID_BACK]->multi_click.max = 3;
    config[BUTTON_ID_BACK]->multi_click.last_click_only = true;
    if (LONG_CLICK_BACK) {
    	config[BUTTON_ID_BACK]->long_click.delay_ms = 400;
    	config[BUTTON_ID_BACK]->long_click.handler = (ClickHandler) back_long_click_handler;
    } else {
        config[BUTTON_ID_BACK]->click.repeat_interval_ms = 400;
    }

}


void buildPlayerWindow() {
	Window* ref = &playerWindow;
	window_init(ref, "Now Playing...");
	window_set_background_color(ref, GColorWhite);

	text_layer_init(&playerTextLayerRow1, GRect(4, 3, 130, 21));
	text_layer_set_background_color(&playerTextLayerRow1, GColorClear);
	text_layer_set_text_color(&playerTextLayerRow1, GColorBlack);
	text_layer_set_text_alignment(&playerTextLayerRow1, GTextAlignmentLeft);
	text_layer_set_font(&playerTextLayerRow1, fonts_get_system_font(FONT_KEY_GOTHIC_18));
	layer_add_child(&ref->layer, &playerTextLayerRow1.layer);

	text_layer_init(&playerTextLayerRow2, GRect(4, 23, 130, 52));
	text_layer_set_background_color(&playerTextLayerRow2, GColorClear);
	text_layer_set_text_color(&playerTextLayerRow2, GColorBlack);
	text_layer_set_text_alignment(&playerTextLayerRow2, GTextAlignmentLeft);
	text_layer_set_font(&playerTextLayerRow2, fonts_get_system_font(FONT_KEY_GOTHIC_24));
	layer_add_child(&ref->layer, &playerTextLayerRow2.layer);

	text_layer_init(&playerTextLayerRow3, GRect(4, 75, 130, 52));
	text_layer_set_background_color(&playerTextLayerRow3, GColorClear);
	text_layer_set_text_color(&playerTextLayerRow3, GColorBlack);
	text_layer_set_text_alignment(&playerTextLayerRow3, GTextAlignmentLeft);
	text_layer_set_font(&playerTextLayerRow3, fonts_get_system_font(FONT_KEY_GOTHIC_24_BOLD));
	layer_add_child(&ref->layer, &playerTextLayerRow3.layer);

	text_layer_init(&playerTextLayerRow4, GRect(4, 127, 130, 22));
	text_layer_set_background_color(&playerTextLayerRow4, GColorClear);
	text_layer_set_text_color(&playerTextLayerRow4, GColorBlack);
	text_layer_set_text_alignment(&playerTextLayerRow4, GTextAlignmentLeft);
	text_layer_set_font(&playerTextLayerRow4, fonts_get_system_font(FONT_KEY_GOTHIC_18));
	layer_add_child(&ref->layer, &playerTextLayerRow4.layer);

	text_layer_init(&playerTextLayer5, GRect(134, 4, 10, 160));
	text_layer_set_background_color(&playerTextLayer5, GColorBlack);
	text_layer_set_text_color(&playerTextLayer5, GColorBlack);
	text_layer_set_text_alignment(&playerTextLayer5, GTextAlignmentLeft);
	layer_add_child(&ref->layer, &playerTextLayer5.layer);
	ref->overrides_back_button = true;

	window_set_click_config_provider(&playerWindow, (ClickConfigProvider) click_config_provider);
}

static void sync_error_callback(DictionaryResult dict_error, AppMessageResult app_message_error, void *context) {
}

void handle_init(AppContextRef ctx) {
    (void)ctx;
    resource_init_current_app(&APP_RESOURCES);
    window_init(&window, "pebbleBMC");
    window_stack_push(&window, true);
    window_set_background_color(&window, GColorWhite);

    text_layer_init(&textLayer, GRect(0, 4, 144, 18));
    text_layer_set_background_color(&textLayer, GColorClear);
    text_layer_set_text_color(&textLayer, GColorBlack);
    text_layer_set_text_alignment(&textLayer, GTextAlignmentLeft);
    text_layer_set_font(&textLayer, fonts_get_system_font(FONT_KEY_GOTHIC_14));
    layer_add_child(&window.layer, &textLayer.layer);
    text_layer_set_text(&textLayer, "Ready");
	text_layer_init(&textLayer2, GRect(0, 22, 144, 22));
	text_layer_set_background_color(&textLayer2, GColorClear);
	text_layer_set_text_color(&textLayer2, GColorBlack);
	text_layer_set_text_alignment(&textLayer2, GTextAlignmentLeft);
	text_layer_set_font(&textLayer2, fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD));
	layer_add_child(&window.layer, &textLayer2.layer);
	text_layer_set_text(&textLayer2, "Reday2!");
	(&window)->overrides_back_button = true;
    window_set_click_config_provider(&window, (ClickConfigProvider) click_config_provider);

    buildPlayerWindow();
    Tuplet initial_values[] = {
      TupletInteger(ON_SHOW_PLAYER, FALSE),
      TupletCString(ON_ROW_1, ""),
      TupletCString(ON_ROW_2, ""),
      TupletCString(ON_ROW_3, "No Information"),
      TupletCString(ON_ROW_4, ""),
    };
	window_set_click_config_provider(&playerWindow, (ClickConfigProvider) click_config_provider);
	  app_sync_init(&sync, sync_buffer, sizeof(sync_buffer), initial_values, ARRAY_LENGTH(initial_values),
	                sync_tuple_changed_callback, sync_error_callback, NULL);
}

void handle_deinit(AppContextRef ctx) {

}

void pbl_main(void *params) {
    PebbleAppHandlers handlers = {
        .init_handler = &handle_init,
        .deinit_handler = &handle_deinit,
        .messaging_info = {
            .buffer_sizes = {
                .inbound = 120,
                .outbound = 64,
            },
        },
    };
    app_event_loop(params, &handlers);
}

