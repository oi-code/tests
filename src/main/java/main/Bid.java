package main;

import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
class Bid {
    @Id
    @GeneratedValue
    private Long id;

    public Bid() {
    }

    public Bid(Item it) {
	item = it;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column(name = "rndint")
    private Integer rnd = ThreadLocalRandom.current().nextInt(42);
}