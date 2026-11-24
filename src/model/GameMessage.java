package model;

import java.io.Serializable;

public record GameMessage(MessageType type, String text)
        implements Serializable {
}