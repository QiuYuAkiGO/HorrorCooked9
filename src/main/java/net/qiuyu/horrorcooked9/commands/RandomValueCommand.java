package net.qiuyu.horrorcooked9.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RandomValueCommand {
    private static final SimpleCommandExceptionType INVALID_RANGE = new SimpleCommandExceptionType(
            Component.literal("Invalid range format. Use 7, 5..10, ..10, 5.. or <min> <max>")
    );
    private static final SimpleCommandExceptionType INVALID_BOUNDS = new SimpleCommandExceptionType(
            Component.literal("Invalid range bounds. min must be <= max")
    );

    private RandomValueCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("random")
                        .then(literal("value")
                                .then(argument("range", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                List.of("7", "5..10", "..10", "5..", "-3..3"),
                                                builder
                                        ))
                                        .executes(context -> randomValueInRange(context, StringArgumentType.getString(context, "range")))
                                )
                        )
        );
    }

    private static int randomValueInRange(CommandContext<CommandSourceStack> context, String rangeText) throws CommandSyntaxException {
        int[] bounds = parseRange(rangeText);
        int min = bounds[0];
        int max = bounds[1];

        int result = (int) ThreadLocalRandom.current().nextLong(min, (long) max + 1L);
        context.getSource().sendSuccess(() -> Component.literal("Random value: " + result), false);
        return result;
    }

    private static int[] parseRange(String rangeText) throws CommandSyntaxException {
        String trimmedRangeText = rangeText.trim();
        if (trimmedRangeText.isEmpty()) {
            throw INVALID_RANGE.create();
        }

        String[] parts = trimmedRangeText.split("\\s+");
        if (parts.length == 2) {
            int min;
            int max;
            try {
                min = Integer.parseInt(parts[0]);
                max = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                throw INVALID_RANGE.create();
            }

            if (min > max) {
                throw INVALID_BOUNDS.create();
            }
            return new int[]{min, max};
        } else if (parts.length > 2) {
            throw INVALID_RANGE.create();
        }

        int separator = trimmedRangeText.indexOf("..");
        int min;
        int max;

        if (separator < 0) {
            // Single value form: "7"
            try {
                min = Integer.parseInt(trimmedRangeText);
            } catch (NumberFormatException ex) {
                throw INVALID_RANGE.create();
            }
            max = min;
        } else {
            String minText = trimmedRangeText.substring(0, separator);
            String maxText = trimmedRangeText.substring(separator + 2);

            if (minText.isEmpty() && maxText.isEmpty()) {
                throw INVALID_RANGE.create();
            }

            try {
                min = minText.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minText);
                max = maxText.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxText);
            } catch (NumberFormatException ex) {
                throw INVALID_RANGE.create();
            }
        }

        if (min > max) {
            throw INVALID_BOUNDS.create();
        }

        return new int[]{min, max};
    }
}
